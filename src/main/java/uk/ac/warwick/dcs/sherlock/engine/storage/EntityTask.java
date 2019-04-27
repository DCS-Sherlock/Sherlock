package uk.ac.warwick.dcs.sherlock.engine.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.api.component.IJob;
import uk.ac.warwick.dcs.sherlock.api.component.ITask;
import uk.ac.warwick.dcs.sherlock.api.component.WorkStatus;
import uk.ac.warwick.dcs.sherlock.engine.storage.BaseStorageFilesystem.IStorable;

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
		this.paramMap = null;
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.hash = null;
		this.secure = null;
		this.status = WorkStatus.PREPARED;

		this.addParams(SherlockRegistry.getDetectorAdjustableParameters(detector));
		this.addParams(SherlockRegistry.getPostProcessorAdjustableParametersFromDetector(detector));
	}

	@Override
	public Class<? extends IDetector> getDetector() {
		try {
			return (Class<? extends IDetector>) Class.forName(this.detector, true, SherlockEngine.classloader);
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
	public long getPersistentId() {
		return this.id;
	}

	@Override
	public List<AbstractModelTaskRawResult> getRawResults() {
		if (this.rawResults == null && this.hash != null && this.hash.length() > 0) {
			this.deserialize();
		}

		return this.rawResults;
	}

	@Override
	public void setRawResults(List<AbstractModelTaskRawResult> rawResults) {
		if (this.rawResults == null && this.hash == null) {
			this.rawResults = rawResults;
			this.serialize();
		}
		else {
			logger.error("task {} already has results", this.id);
		}
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

	void setStatus(WorkStatus status) {
		this.status = status;
	}

	@Override
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	@Override
	public boolean hasResults() {
		return this.hash != null && this.hash.length() > 0;
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

	@Override
	public void setComplete() {
		this.status = WorkStatus.COMPLETE;
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
			BaseStorage.logger
					.warn("Could not set adjustable parameter '{}' for job#{} detector '{}', parameter passed is not an integer", paramObj.getName(), this.job.getPersistentId(), this.detector);
			return false;
		}

		if (value < paramObj.getMinimumBound() || value > paramObj.getMaximumBound()) {
			BaseStorage.logger.warn("Could not set adjustable parameter '{}' for job#{} detector '{}', value passed is outside the parameter bounds", paramObj.getName(), this.job.getPersistentId(),
					this.detector);
			return false;
		}

		this.paramMap.put(paramObj.getReference(), value);
		BaseStorage.instance.database.storeObject(this);
		return true;
	}

	void remove() {
		BaseStorage.instance.filesystem.removeTaskRawResults(this);
		BaseStorage.instance.database.removeObject(this);
	}

	void setRawResultsNoStore(List<AbstractModelTaskRawResult> rawResults) {
		this.rawResults = rawResults;
	}

	private void addParams(List<AdjustableParameterObj> params) {
		if (params != null && !params.isEmpty()) {
			if (this.paramMap == null) {
				this.paramMap = new HashMap<>();
			}

			params.forEach(x -> this.paramMap.put(x.getReference(), x.getDefaultValue()));
		}
	}

	private void deserialize() {
		BaseStorage.instance.filesystem.loadTaskRawResults(this);
	}

	private void serialize() {
		BaseStorage.instance.filesystem.storeTaskRawResults(this);
		BaseStorage.instance.database.storeObject(this);
	}

}
