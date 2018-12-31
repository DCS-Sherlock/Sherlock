package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.engine.model.IJob;
import uk.ac.warwick.dcs.sherlock.engine.model.ITask;
import uk.ac.warwick.dcs.sherlock.engine.model.IWorkspace;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Job")
public class EntityJob implements IJob, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne (fetch = FetchType.LAZY, optional = false)
	private EntityWorkspace workspace;

	@OneToMany (mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityTask> tasks = new ArrayList<>();

	public EntityJob() {
		super();
	}

	public EntityJob(EntityWorkspace workspace) {
		super();
		this.workspace = workspace;
	}

	@Override
	public ITask createTask() {
		return null;
	}

	public long getId() {
		return this.id;
	}

	@Override
	public List<ITask> getTasks() {
		return new ArrayList<>(this.tasks);
	}

	@Override
	public IWorkspace getWorkspace() {
		return this.workspace;
	}
}
