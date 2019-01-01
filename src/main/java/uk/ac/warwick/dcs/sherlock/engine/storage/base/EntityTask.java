package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.data.AbstractModelRawResult;
import uk.ac.warwick.dcs.sherlock.engine.model.IJob;
import uk.ac.warwick.dcs.sherlock.engine.model.ITask;
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
	private int rank;

	private Timestamp timestamp;
	private String hash;
	private byte[] secure;

	// When adding check that all same type
	// Store as a file in case too large for db field, store refs to files in this object
	private transient List<AbstractModelRawResult> rawResults;

	//private List<ModelProcessedResults> listFinalResults;

	public EntityTask() {
		super();
	}

	private void deserialize() {
		BaseStorage.instance.filesystem.loadTaskRawResults(this);
		this.rawResults.forEach(System.out::println);
	}

	public EntityTask(EntityJob job, IDetector detector) {
		super();
		this.job = job;
		this.detector = detector.getClass().getName();
		this.rank = detector.getRank().ordinal();
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.hash = null;
		this.secure = null;
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
	public long getPersistentId() {
		return this.id;
	}

	@Override
	public IDetector.Rank getRank() {
		return IDetector.Rank.values()[rank];
	}

	@Override
	public List<AbstractModelRawResult> getRawResults() {
		if (this.rawResults == null && this.hash != null && this.hash.length() > 0) {
			this.deserialize();
		}

		return this.rawResults;
	}

	@Override
	public void setRawResults(List<AbstractModelRawResult> rawResults) {
		this.rawResults = rawResults;
		this.serialize();
	}

	private void serialize() {
		BaseStorage.instance.filesystem.storeTaskRawResults(this);
		BaseStorage.instance.database.storeObject(this);
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
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	void setRawResultsNoStore(List<AbstractModelRawResult> rawResults) {
		this.rawResults = rawResults;
	}

}
