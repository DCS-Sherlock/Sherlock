package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Result")
public class EntityResult implements Serializable {

	private static final long serialVersionUID = 1L;
	//private static Logger logger = LoggerFactory.getLogger(EntityResult.class);

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	private EntityFile file;

	@ManyToOne (fetch = FetchType.LAZY, optional = false)
	private EntityJob job;

	private float overallScore;
	private Map<EntityFile, Float> overallFileScores;

	List<ResultTask> taskResults;

	public EntityResult() {
		super();
	}

	public EntityResult(EntityJob job) {
		super();
		this.job = job;
	}



	@Embeddable
	public class ResultTask implements Serializable {
		private static final long serialVersionUID = 1L;

		private EntityTask task;

		private float taskScore;
		private Map<EntityFile, Float> fileScores;

		private List<ResultBlock> blocks;
	}

	@Embeddable
	public class ResultBlockFamily implements Serializable {
		private static final long serialVersionUID = 1L;

		private ResultBlock parent;
		private List<ResultBlock> children;
	}

	@Embeddable
	public class ResultBlock implements Serializable {
		private static final long serialVersionUID = 1L;

		private EntityFile file;

		private int startLine;
		private int endLine;
		private float score;
	}
}
