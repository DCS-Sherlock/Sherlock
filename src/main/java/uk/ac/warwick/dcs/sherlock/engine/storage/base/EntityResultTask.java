package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultTask;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.*;

@Entity (name = "ResultTask")
public class EntityResultTask implements IResultTask, Serializable {

	private static final long serialVersionUID = 1L;

	private EntityTask task;
	private float taskScore;

	private Map<EntityFile, Float> fileScores;
	private List<EntityCodeBlockGroup> containingBlocks;

	EntityResultTask() {
		super();
	}

	EntityResultTask(EntityTask task) {
		super();
		this.task = task;
		this.taskScore = 0;

		this.fileScores = new HashMap<>();
		this.containingBlocks = new LinkedList<>();
	}

	@Override
	public void addFileScore(ISourceFile file, float score) {

	}

	@Override
	public List<ICodeBlockGroup> getContainingBlocks() {
		return null;
	}

	@Override
	public Map<ISourceFile, Float> getFileScores() {
		return null;
	}

	@Override
	public ITask getTask() {
		return null;
	}

	@Override
	public float getTaskScore() {
		return 0;
	}

	@Override
	public void setTaskScore(float score) {

	}
}
