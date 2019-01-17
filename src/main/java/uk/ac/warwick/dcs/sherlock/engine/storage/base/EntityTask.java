package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector.Rank;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;
import uk.ac.warwick.dcs.sherlock.engine.component.WorkStatus;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.BaseStorageFilesystem.IStorable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Entity (name = "Task")
public class EntityTask implements ITask, IStorable, Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(EntityTask.class);

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne (fetch = FetchType.LAZY, optional = false)
	private EntityJob job;

	private String detector;
	private Map<String, Float> paramMap;
	private Rank rank;

	private Timestamp timestamp;
	private String hash;
	private byte[] secure;

	// When adding check that all same type
	// Store as a file in case too large for db field, store refs to files in this object
	private transient List<AbstractModelTaskRawResult> rawResults;

	private WorkStatus status;

	public EntityTask() {
		super();
	}

	public EntityTask(EntityJob job, Class<? extends IDetector> detector) {
		super();
		this.job = job;
		this.detector = detector.getName();
		this.rank = SherlockRegistry.getDetectorRank(detector);
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.hash = null;
		this.secure = null;
		this.status = WorkStatus.PREPARED;

		List<AdjustableParameterObj> params = SherlockRegistry.getDetectorAdjustableParameters(detector);

		if (params != null) {
			List<AdjustableParameterObj> params2 = SherlockRegistry.getPostProcessorAdjustableParametersFromDetector(detector);
			if (params2 != null) {
				params.addAll(params2);
			}
		}
		else {
			params = SherlockRegistry.getPostProcessorAdjustableParametersFromDetector(detector);
		}
		
		if (params != null && params.isEmpty()) {
			this.paramMap = new HashMap<>();
			params.forEach(x -> {
				if (!this.paramMap.containsKey(x.getReference())) {
					this.paramMap.put(x.getReference(), x.getDefaultValue());
				}
			});
		}
		else {
			this.paramMap =  null;
		}
	}

	@Override
	public Class<? extends IDetector> getDetector() {
		try {
			return (Class<? extends IDetector>) Class.forName(this.detector);
		}
		catch (Exception e) {
			logger.error("Issue getting detector for task {}", this.id);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getHash() {
		return this.hash;
	}

	@Override
	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public IJob getJob() {
		return this.job;
	}

	@Override
	public Map<String, Float> getParameterMapping() {
		return this.paramMap;
	}

	@Override
	public IDetector.Rank getRank() {
		return rank;
	}

	@Override
	public List<AbstractModelTaskRawResult> getRawResults() {
		if (this.rawResults == null && this.hash != null && this.hash.length() > 0) {
			this.deserialize();
		}

		return this.rawResults;
	}

	private void deserialize() {
		BaseStorage.instance.filesystem.loadTaskRawResults(this);
	}

	@Override
	public void setRawResults(List<AbstractModelTaskRawResult> rawResults) {
		this.rawResults = rawResults;
		this.setComplete();
		this.serialize();
	}

	private void serialize() {
		BaseStorage.instance.filesystem.storeTaskRawResults(this);
		BaseStorage.instance.database.storeObject(this);
	}

	@Override
	public void setComplete() {
		this.status = WorkStatus.COMPLETE;
	}

	@Override
	public byte[] getSecureParam() {
		return this.secure;
	}

	@Override
	public void setSecureParam(byte[] secure) {
		this.secure = secure;
	}

	@Override
	public WorkStatus getStatus() {
		return this.status;
	}

	@Override
	public boolean hasResults() {
		return this.hash != null && this.hash.length() > 0;
	}

	void setStatus(WorkStatus status) {
		this.status = status;
	}

	@Override
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	@SuppressWarnings ("Duplicates")
	@Override
	public boolean resetParameter(AdjustableParameterObj paramObj) {
		if (paramObj == null || !this.paramMap.containsKey(paramObj.getReference())) {
			BaseStorage.logger.warn("Could not reset adjustable parameter for job#{} detector '{}', parameter passed is null", this.job.getPersistentId(), this.detector);
			return false;
		}

		if (paramObj.isFixed() && (this.status == WorkStatus.COMPLETE || this.hasResults())) {
			BaseStorage.logger.warn("Parameter '{}' for job#{} detector '{}', cannot be modified after a task is run", paramObj.getName(), this.job.getPersistentId(), this.detector);
			return false;
		}

		return this.setParameter(paramObj, paramObj.getDefaultValue());
	}

	@SuppressWarnings ("Duplicates")
	@Override
	public boolean setParameter(AdjustableParameterObj paramObj, float value) {
		if (paramObj == null || !this.paramMap.containsKey(paramObj.getReference())) {
			BaseStorage.logger.warn("Could not set adjustable parameter for job#{} detector '{}', parameter passed is null", this.job.getPersistentId(), this.detector);
			return false;
		}

		if (paramObj.isFixed() && (this.status == WorkStatus.COMPLETE || this.hasResults())) {
			BaseStorage.logger.warn("Parameter '{}' for job#{} detector '{}', cannot be modified after a task is run", paramObj.getName(), this.job.getPersistentId(), this.detector);
			return false;
		}

		if (paramObj.isInt() && value % 1 != 0) {
			BaseStorage.logger.warn("Could not set adjustable parameter '{}' for job#{} detector '{}', parameter passed is not an integer", paramObj.getName(), this.job.getPersistentId(), this.detector);
			return false;
		}

		if (value < paramObj.getMinimumBound() || value > paramObj.getMaximumBound()) {
			BaseStorage.logger.warn("Could not set adjustable parameter '{}' for job#{} detector '{}', value passed is outside the parameter bounds", paramObj.getName(), this.job.getPersistentId(), this.detector);
			return false;
		}

		this.paramMap.put(paramObj.getReference(), value);
		return true;
	}

	@Override
	public long getPersistentId() {
		return this.id;
	}

	void setRawResultsNoStore(List<AbstractModelTaskRawResult> rawResults) {
		this.rawResults = rawResults;
	}

}
