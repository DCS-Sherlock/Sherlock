package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.engine.component.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;

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

	// list of file ids in workspace WHEN creating job, used to warn and prevent report gen if file is removed or updated(remove existing and add updated file as new entity when doing this)
	private long[] filesPresent;

	@OneToMany (mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<EntityTask> tasks = new ArrayList<>();

	@OneToOne (mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private EntityResultJob results = null;

	public EntityJob() {
		super();
		this.prepared = true;
		this.detectors = null;
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

		return true;
	}

	@Override
	public IResultJob createNewResult() {
		if (this.results != null) {
			this.results.remove();
		}

		this.results = new EntityResultJob(this);
		BaseStorage.instance.database.storeObject(this.results);
		return this.results;
	}

	@Override
	public long[] getFiles() {
		return this.filesPresent;
	}

	public List<Long> getFilesList() {
		return Arrays.stream(this.filesPresent).boxed().collect(Collectors.toList());
	}

	@Override
	public IResultJob getLatestResult() {
		BaseStorage.instance.database.refreshObject(this);
		return this.results;
	}

	@Override
	public long getPersistentId() {
		return this.id;
	}

	@Override
	public WorkStatus getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(WorkStatus status) {
		this.status = status;
		BaseStorage.instance.database.storeObject(this);
	}

	@Override
	public List<ITask> getTasks() {
		BaseStorage.instance.database.refreshObject(this);
		return new ArrayList<>(this.tasks);
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
	public boolean isPrepared() {
		return this.prepared;
	}

	@Override
	public boolean prepare() {
		this.workspace.getJobs().add(this);

		this.detectors.forEach(x -> {
			EntityTask newTask = new EntityTask(this, x);
			this.tasks.add(newTask);
			BaseStorage.instance.database.storeObject(newTask);
		});

		this.setStatus(WorkStatus.PREPARED);
		BaseStorage.instance.database.storeObject(this);

		this.prepared = true;
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
	public void remove() {

		if (this.tasks != null) {
			for (EntityTask t : this.tasks) {
				t.remove();
			}
		}

		if (this.results != null) {
			this.results.remove();
		}

		BaseStorage.instance.database.refreshObject(this);
		BaseStorage.instance.database.removeObject(this);
	}
}
