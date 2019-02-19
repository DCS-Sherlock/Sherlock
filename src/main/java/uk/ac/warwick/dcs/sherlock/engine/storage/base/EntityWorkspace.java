package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.*;

@Entity (name = "Workspace")
public class EntityWorkspace implements IWorkspace, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	private String name;
	private String lang;

	@OneToMany (mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityArchive> submissions;

	@OneToMany (mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityJob> jobs;

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
		if (this.submissions == null) {
			return null;
		}
		
		return this.submissions.stream().map(EntityArchive::getAllFiles).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
	}

	@Override
	public List<ISubmission> getSubmissions() {
		BaseStorage.instance.database.refreshObject(this);
		if (this.submissions == null) {
			return null;
		}

		return new LinkedList<>(this.submissions);
	}

	@Override
	public List<IJob> getJobs() {
		BaseStorage.instance.database.refreshObject(this);
		if (this.jobs == null) {
			return null;
		}
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
