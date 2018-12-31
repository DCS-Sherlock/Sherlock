package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.engine.model.IJob;
import uk.ac.warwick.dcs.sherlock.engine.model.IWorkspace;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Workspace")
public class EntityWorkspace implements IWorkspace, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
		return null;
	}

	public List<EntityFile> getFiles() {
		return this.files;
	}

	public long getId() {
		return this.id;
	}

	@Override
	public List<IJob> getJobs() {
		return new ArrayList<>(this.jobs);
	}

	public String getName() {
		return this.name;
	}

}
