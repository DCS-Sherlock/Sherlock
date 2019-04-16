package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.WorkStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Archive")
public class EntityArchive implements ISubmission, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	private String name;
	private boolean pending;

	@ManyToOne (fetch = FetchType.LAZY)
	private EntityWorkspace workspace;

	@Transient
	EntityWorkspace pendingWorkspace;

	@ManyToOne (fetch = FetchType.LAZY)
	private EntityArchive parent;

	@OneToMany (mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<EntityArchive> children = new ArrayList<>();

	@OneToMany (mappedBy = "archive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<EntityFile> files = new ArrayList<>();

	EntityArchive() {
		super();
	}

	EntityArchive(String name) {
		this(null, name, null);
	}

	EntityArchive(EntityWorkspace pendingWorkspace, String name) {
		this(pendingWorkspace, name, null);
	}

	EntityArchive(String name, EntityArchive archive) {
		this(null, name, archive);
	}

	EntityArchive(EntityWorkspace pendingWorkspace, String name, EntityArchive archive) {
		super();
		this.name = name;
		this.pending = pendingWorkspace != null;
		this.parent = archive;
		this.workspace = null;
		this.pendingWorkspace = pendingWorkspace;

		if (archive != null) {
			archive.getChildren_().add(this);
			this.pending = archive.pending;
		}
	}

	@Override
	public int compareTo(@NotNull ISubmission o) {
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
		return this.name;
	}

	@Override
	public ISubmission getParent() {
		return this.parent;
	}

	void setParent(EntityArchive archive) {
		this.parent = archive;
		this.parent.getChildren_().add(this);

		BaseStorage.instance.database.storeObject(this);

		List<ISourceFile> children = this.getAllFilesRecursive(new LinkedList<>());
		children.forEach(f -> BaseStorage.instance.filesystem.updateFileArchive((EntityFile) f, ((EntityFile) f).getArchive()));
	}

	@Override
	public int getTotalFileCount() {
		return this.parent != null ? this.parent.getTotalFileCount() : this.getFileCount();
	}

	@Override
	public boolean hasParent() {
		return this.parent != null;
	}

	@Override
	public void remove() {
		//Set all the jobs as having missing files
		if (this.getWorkspace() != null) {
			for (IJob job : this.getWorkspace().getJobs()) {
				job.setStatus(WorkStatus.MISSING_FILES);
			}
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

		try {
			BaseStorage.instance.database.refreshObject(this);
			BaseStorage.instance.database.removeObject(this);
		}
		catch (Exception ignored) {
		}
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

	List<EntityArchive> getChildren_() {
		return this.children;
	}

	List<EntityFile> getFiles() {
		BaseStorage.instance.database.refreshObject(this);
		return this.files;
	}

	List<EntityFile> getFiles_() {
		return this.files;
	}

	EntityArchive getParent_() {
		return this.parent;
	}

	EntityWorkspace getWorkspace() {
		return this.parent != null ? this.parent.getWorkspace() : this.workspace;
	}

	void setSubmissionArchive(EntityWorkspace workspace) {
		this.workspace = workspace;
		this.parent = null;
	}

	void writeToPendingWorkspace() {
		if (this.pending && this.pendingWorkspace != null && this.workspace == null) {
			this.pendingWorkspace.getSubmissions().stream().filter(s -> s.getName().equals(this.name)).forEach(ISubmission::remove);

			this.setPendingRecursive(false);
			this.setSubmissionArchive(this.pendingWorkspace);
			BaseStorage.instance.database.storeObject(this);
			BaseStorage.instance.database.refreshObject(this.workspace);
		}
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

	private void setPendingRecursive(boolean pending) {
		this.children.forEach(a -> a.setPendingRecursive(pending));
		this.pending = pending;
	}

	private List<ISourceFile> getAllFilesRecursive(List<ISourceFile> files) {
		BaseStorage.instance.database.refreshObject(this);
		if (this.children != null) {
			for (EntityArchive child : this.children) {
				child.getAllFilesRecursive(files);
			}
		}

		if (this.files != null && this.files.size() > 0) {
			files.addAll(this.files);
		}

		return files;
	}
}
