package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;

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
	private String lang;

	@OneToMany (mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityFile> files = new ArrayList<>();

	@OneToMany (mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityJob> jobs = new ArrayList<>();

	public EntityWorkspace() {
		super();
		this.name = null;
		this.lang = null;
	}

	public EntityWorkspace(String name, String lang) {
		super();
		this.storeName(name);
		this.lang = lang;
	}

	@Override
	public IJob createJob() {
		return new EntityJob(this);
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
	public String getLanguage() {
		return this.lang;
	}

	@Override
	public void setLanguage(String lang) {
		this.lang = lang;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.storeName(name);
	}

	@Override
	public long getPersistentId() {
		return id;
	}

	private void storeName(String name) {
		if (name.length() > 64) {
			BaseStorage.logger.warn("Workspace name too long [{}]", name);
			this.name = name.substring(0, 64);
		}
		else {
			this.name = name;
		}
	}

}