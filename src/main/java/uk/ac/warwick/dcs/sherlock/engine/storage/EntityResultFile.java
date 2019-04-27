package uk.ac.warwick.dcs.sherlock.engine.storage;

import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.component.IResultFile;
import uk.ac.warwick.dcs.sherlock.api.component.IResultTask;
import uk.ac.warwick.dcs.sherlock.api.component.ITask;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "ResultFile")
public class EntityResultFile implements IResultFile, Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	private EntityResultJob jobRes;

	private EntityFile file;
	private float overallScore;

	private Map<EntityFile, Float> fileScores;

	@OneToMany (mappedBy = "fileRes", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<EntityResultTask> taskResults;

	EntityResultFile() {
		super();
	}

	EntityResultFile(EntityResultJob jobRes, EntityFile file) {
		super();
		this.jobRes = jobRes;
		this.file = file;
		this.overallScore = 0;

		this.fileScores = new HashMap<>();
		this.taskResults = new LinkedList<>();
	}

	@Override
	public void addFileScore(ISourceFile file, float score) {
		if (file instanceof EntityFile) {
			this.fileScores.put((EntityFile) file, score);
		}
	}

	@Override
	public IResultTask addTaskResult(ITask task) {
		//Check task not in results first!!!

		if (task instanceof EntityTask) {
			EntityResultTask t = new EntityResultTask(this, (EntityTask) task);
			this.taskResults.add(t);
			BaseStorage.instance.database.storeObject(t);
			return t;
		}

		return null;
	}

	@Override
	public ISourceFile getFile() {
		return this.file;
	}

	@Override
	public float getFileScore(ISourceFile file) {
		if (file instanceof EntityFile) {
			return this.fileScores.getOrDefault(file, 0f);
		}

		return 0;
	}

	@Override
	public Map<ISourceFile, Float> getFileScores() {
		return new HashMap<>(this.fileScores);
	}

	@Override
	public float getOverallScore() {
		return this.overallScore;
	}

	@Override
	public void setOverallScore(float score) {
		this.overallScore = score;
	}

	@Override
	public List<IResultTask> getTaskResults() {
		return new LinkedList<>(this.taskResults);
	}

	void remove() {
		if (this.taskResults != null) {
			for (EntityResultTask t : this.taskResults) {
				t.remove();
			}
		}
		BaseStorage.instance.database.removeObject(this);
	}

	List<Object> store() {
		List<Object> list = new LinkedList<>(this.taskResults);
		list.add(this);
		return list;
	}
}
