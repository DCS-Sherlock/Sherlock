package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Result")
public class EntityResult implements Serializable {

	private static final long serialVersionUID = 1L;

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
	private class ResultTask implements Serializable {
		private static final long serialVersionUID = 1L;

		private EntityTask task;

		private float taskScore;
		private Map<EntityFile, Float> fileScores;

		private List<EntityCodeBlockGroup> containingBlocks;
	}
}
