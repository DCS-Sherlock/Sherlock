package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.engine.component.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Entity (name = "Job")
public class EntityJob implements IJob, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne (fetch = FetchType.LAZY, optional = false)
	private EntityWorkspace workspace;

	private Timestamp timestamp;

	private WorkStatus status;

	@Transient
	private boolean prepared;

	@Transient
	private List<Class<? extends IDetector>> detectors;

	@Transient
	private Map<String, ITuple<Class<? extends IDetector>, Float>> paramMap;

	// list of file ids in workspace WHEN creating job, used to warn and prevent report gen if file is removed or updated(remove existing and add updated file as new entity when doing this)
	private long[] filesPresent;

	@OneToMany (mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityTask> tasks = new ArrayList<>();

	@OneToMany (mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityResult> results = new ArrayList<>();

	public EntityJob() {
		super();
		this.prepared = true;
		this.detectors = null;
		this.paramMap = null;
	}

	public EntityJob(EntityWorkspace workspace) {
		super();
		this.workspace = workspace;
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.status = WorkStatus.NOT_PREPARED;
		this.filesPresent = new long[workspace.getFiles().size()];

		for (int i = 0; i < this.filesPresent.length; i++) {
			this.filesPresent[i] = workspace.getFiles().get(i).getPersistentId();
		}

		this.prepared = false;
		this.detectors = new LinkedList<>();
		this.paramMap = new HashMap<>();
	}

	@Override
	public boolean prepare() {
		this.workspace.getJobs().add(this);
		BaseStorage.instance.database.storeObject(this);

		this.detectors.forEach(x -> {
			Map<String, Float> map = new HashMap<>();
			this.paramMap.forEach((k,v) -> {
				if (v.getKey().equals(x)) { //if is correct detector
					map.put(k, v.getValue());
				}
			});

			EntityTask newTask = new EntityTask(this, x, map.isEmpty() ? null : map);
			this.tasks.add(newTask);
			BaseStorage.instance.database.storeObject(newTask);
		});

		this.status = WorkStatus.PREPARED;
		this.prepared = true;
		return true;
	}

	@Override
	public boolean isPrepared() {
		return this.prepared;
	}

	@Override
	public boolean addDetector(Class<? extends IDetector> det) {
		if (this.isPrepared()) {
			BaseStorage.logger.warn("Could not add detector for job#{}, job already prepared", this.getPersistentId());
			return false;
		}

		if (det == null) {
			BaseStorage.logger.warn("Could not add detector for job#{}, is null", this.getPersistentId());
			return false;
		}

		if (this.detectors.contains(det)) {
			BaseStorage.logger.warn("Could not add detector {} for job#{}, is already present", det.getName(), this.getPersistentId());
			return false;
		}

		this.detectors.add(det);

		List<AdjustableParameterObj> params = SherlockRegistry.getDetectorAdjustableParameters(det);
		if (params == null || params.isEmpty()) {
			return false;
		}

		params.forEach(x -> {
			if (!this.paramMap.containsKey(x.getReference())) {
				ITuple t = new Tuple(det, x.getDefaultValue());
				this.paramMap.put(x.getReference(), t);
			}
		});

		return true;
	}

	@Override
	public boolean removeDetector(Class<? extends IDetector> det) {
		if (this.isPrepared()) {
			BaseStorage.logger.warn("Could not add detector for job#{}, job already prepared", this.getPersistentId());
			return false;
		}

		if (det == null || !this.detectors.contains(det)) {
			BaseStorage.logger.warn("Could not remove detector for job#{}, is null or not present", this.getPersistentId());
			return false;
		}

		this.detectors.remove(det);

		return true;
	}

	@Override
	public boolean setParameter(AdjustableParameterObj paramObj, float value) {
		if (this.isPrepared()) {
			BaseStorage.logger.warn("Could not add detector for job#{}, job already prepared", this.getPersistentId());
			return false;
		}

		if (paramObj == null || !this.paramMap.containsKey(paramObj.getReference())) {
			BaseStorage.logger.warn("Could not set adjustable parameter for job#{}, parameter passed is null", this.getPersistentId());
			return false;
		}

		if (paramObj.isInt() && value % 1 != 0) {
			BaseStorage.logger.warn("Could not set adjustable parameter for job#{}, parameter passed is not an integer", this.getPersistentId());
			return false;
		}

		if (value < paramObj.getMinimumBound() || value > paramObj.getMaxumumBound()) {
			BaseStorage.logger.warn("Could not set adjustable parameter for job#{}, value passed is outside the parameter bounds", this.getPersistentId());
			return false;
		}

		this.paramMap.get(paramObj.getReference()).setValue(value);
		return true;
	}

	@Override
	public boolean resetParameter(AdjustableParameterObj paramObj) {
		return this.setParameter(paramObj, paramObj.getDefaultValue());
	}

	@Override
	public long getPersistentId() {
		return this.id;
	}

	@Override
	public List<ITask> getTasks() {
		BaseStorage.instance.database.refreshObject(this);
		return new ArrayList<>(this.tasks);
	}

	@Override
	public long[] getFiles() {
		return this.filesPresent;
	}

	@Override
	public LocalDateTime getTimestamp() {
		return this.timestamp.toLocalDateTime();
	}

	@Override
	public IWorkspace getWorkspace() {
		return this.workspace;
	}

	@Override
	public List<IJobResult> getResults() {
		//TODO: do results api so we can write the getter
		return null;
	}

	@Override
	public WorkStatus getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(WorkStatus status) {
		this.status = status;
	}
}
