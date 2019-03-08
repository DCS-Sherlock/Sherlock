package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.engine.component.WorkStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Archive")
public class EntityArchive implements ISubmission, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	private String name;

	@ManyToOne (fetch = FetchType.LAZY)
	private EntityWorkspace workspace;

	@ManyToOne (fetch = FetchType.LAZY)
	private EntityArchive parent;

	@OneToMany (mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityArchive> children;

	@OneToMany (mappedBy = "archive", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityFile> files;

	EntityArchive() {
		super();
	}

	EntityArchive(String name) {
		this(name, null);
	}

	EntityArchive(String name, EntityArchive archive) {
		super();
		this.name = name;
		this.parent = archive;
		this.workspace = null;
	}

	@Override
	public int compareTo(ISubmission o) {
		return this.name.compareTo(o.getName());
	}

	@Override
	public boolean equals(ISubmission o) {
		return o.getId() == this.id;
	}

	@Override
	public List<ISourceFile> getAllFiles() {
		return this.parent == null ? this.getAllFilesRecursive(new LinkedList<>()) : this.parent.getAllFiles();
	}

	@Override
	public List<ISubmission> getContainedDirectories() {
		BaseStorage.instance.database.refreshObject(this);
		return new LinkedList<>(this.getChildren());
	}

	@Override
	public List<ISourceFile> getContainedFiles() {
		BaseStorage.instance.database.refreshObject(this);
		return new LinkedList<>(this.getFiles());
	}

	@Override
	public int getFileCount() {
		BaseStorage.instance.database.refreshObject(this);
		return (this.files != null ? this.files.size() : 0) + (this.children != null ? this.children.stream().mapToInt(EntityArchive::getFileCount).sum() : 0);
	}

	@Override
	public long getId() {
		return this.parent == null ? this.id : this.parent.getId();
	}

	@Override
	public String getName() {
		return name;
	}

	public EntityArchive getParent() {
		return this.parent;
	}

	@Override
	public int getTotalFileCount() {
		return this.parent != null ? this.parent.getTotalFileCount() : this.getFileCount();
	}

	@Override
	public void remove() {
		//Set all the jobs as having missing files
		for (IJob job : this.getWorkspace().getJobs()) {
			job.setStatus(WorkStatus.MISSING_FILES);
		}

		BaseStorage.instance.database.refreshObject(this);
		if (this.children != null) {
			for (EntityArchive child : this.children) {
				child.remove();
			}
		}

		if (this.files != null) {
			files.forEach(EntityFile::remove_);
		}

		BaseStorage.instance.database.refreshObject(this);
		BaseStorage.instance.database.removeObject(this);
		BaseStorage.instance.database.refreshObject(this.workspace);
	}

	void clean() {
		if (this.parent != null) {
			this.parent.clean();
		}
		else {
			this.cleanRecursive();
		}
	}

	List<EntityArchive> getChildren() {
		BaseStorage.instance.database.refreshObject(this);
		return this.children;
	}

	List<EntityFile> getFiles() {
		BaseStorage.instance.database.refreshObject(this);
		return this.files;
	}

	EntityWorkspace getWorkspace() {
		return this.parent != null ? this.parent.getWorkspace() : this.workspace;
	}

	void setSubmissionArchive(EntityWorkspace workspace) {
		this.workspace = workspace;
		this.parent = null;
	}

	private void cleanRecursive() {
		BaseStorage.instance.database.refreshObject(this);
		if (this.children != null) {
			for (EntityArchive child : this.children) {
				child.cleanRecursive();
			}
		}

		if (this.getFileCount() == 0) {
			this.remove();
		}
	}

	private List<ISourceFile> getAllFilesRecursive(List<ISourceFile> files) {
		BaseStorage.instance.database.refreshObject(this);
		if (this.children != null) {
			for (EntityArchive child : this.children) {
				child.getAllFilesRecursive(files);
			}
		}

		if (this.files != null) {
			files.addAll(this.files);
		}

		return files;
	}
}
