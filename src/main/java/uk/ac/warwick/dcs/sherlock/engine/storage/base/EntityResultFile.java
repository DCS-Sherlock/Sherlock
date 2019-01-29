package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultFile;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultTask;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.*;

@Entity (name = "ResultFile")
public class EntityResultFile implements IResultFile, Serializable {

	private static final long serialVersionUID = 1L;

	private EntityFile file;
	private float overallScore;

	private Map<EntityFile, Float> overallFileScores;
	private List<EntityResultTask> taskResults;

	EntityResultFile() {
		super();
	}

	EntityResultFile(EntityFile file) {
		super();
		this.file = file;
		this.overallScore = 0;

		this.overallFileScores = new HashMap<>();
		this.taskResults = new LinkedList<>();
	}

	/*@Override
	public boolean addFileScore(ISourceFile file, float score) {
		return false;
	}*/

	@Override
	public IResultTask addTaskResult(ITask task) {
		//Check task not in results first!!!

		if (task instanceof EntityTask) {
			EntityResultTask t = new EntityResultTask((EntityTask) task);
			this.taskResults.add(t);
			return t;
		}

		return null;
	}

	@Override
	public ISourceFile getFile() {
		return this.file;
	}

	@Override
	public Map<ISourceFile, Float> getOverallFileScores() {
		return new HashMap<>(this.overallFileScores);
	}

	@Override
	public float getOverallScore() {
		return this.overallScore;
	}

	/*@Override
	public void setOverallScore(float score) {
		this.overallScore = score;
	}*/

	@Override
	public List<IResultTask> getTaskResults() {
		return new LinkedList<>(this.taskResults);
	}

	List<Object> store() {
		List<Object> list = new LinkedList<>(this.taskResults);
		list.add(this);
		return list;
	}
}
