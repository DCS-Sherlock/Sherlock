package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.data.AbstractModelRawResult;
import uk.ac.warwick.dcs.sherlock.engine.model.IJob;
import uk.ac.warwick.dcs.sherlock.engine.model.ITask;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Task")
public class EntityTask implements ITask, Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(EntityTask.class);

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne (fetch = FetchType.LAZY, optional = false)
	private EntityJob job;

	private String detector;
	private int rank;

	// When adding check that all same type
	// Store as a file in case too large for db field, store refs to files in this object
	private transient List<AbstractModelRawResult> rawResults;

	//private List<ModelProcessedResults> listFinalResults;

	public EntityTask() {
		super();
	}

	public EntityTask(EntityJob job, IDetector detector) {
		super();
		this.job = job;
		this.detector = detector.getClass().getName();
		this.rank = detector.getRank().ordinal();
		this.rawResults = new LinkedList<>();
	}

	public void deserialize() {

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

	public long getId() {
		return id;
	}

	@Override
	public IJob getJob() {
		return this.job;
	}

	@Override
	public IDetector.Rank getRank() {
		return IDetector.Rank.values()[rank];
	}

	@Override
	public List<AbstractModelRawResult> getRawResults() {
		return this.rawResults;
	}

	public void serialize() {

	}

}
