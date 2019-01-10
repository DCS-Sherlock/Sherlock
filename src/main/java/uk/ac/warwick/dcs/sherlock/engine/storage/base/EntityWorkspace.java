package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.model.IJob;
import uk.ac.warwick.dcs.sherlock.engine.model.IWorkspace;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Workspace")
public class EntityWorkspace implements IWorkspace, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	private String name;

	@OneToMany (mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityFile> files = new ArrayList<>();

	@OneToMany (mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityJob> jobs = new ArrayList<>();

	public EntityWorkspace() {
		super();
	}

	public EntityWorkspace(String name) {
		super();
		this.name = name;
	}

	@Override
	public IJob createJob() {
		EntityJob newJob = new EntityJob(this);
		this.jobs.add(newJob);
		BaseStorage.instance.database.storeObject(newJob);
		return newJob;
	}

	@Override
	public List<ISourceFile> getFiles() {
		BaseStorage.instance.database.refreshObject(this);
		return new LinkedList<>(this.files);
	}

	@Override
	public List<IJob> getJobs() {
		BaseStorage.instance.database.refreshObject(this);
		return new LinkedList<>(this.jobs);
	}

	@Override
	public long getPersistentId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

}
