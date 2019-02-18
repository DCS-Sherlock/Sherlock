package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;

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

	public EntityArchive() {
		super();
	}

	public EntityArchive(String name) {
		this(name, null);
	}

	public EntityArchive(String name, EntityArchive archive) {
		super();
		this.name = name;
		this.parent = archive;
		this.workspace = null;
	}

	public List<EntityArchive> getChildren() {
		BaseStorage.instance.database.refreshObject(this);
		return this.children;
	}

	@Override
	public List<ISourceFile> getAllFiles() {
		return this.parent == null ? this.getAllFilesRecursive(new LinkedList<>()) : this.parent.getAllFiles();
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

	public List<EntityFile> getFiles() {
		BaseStorage.instance.database.refreshObject(this);
		return this.files;
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

	/**
	 * Calculates the file count of this directory and all subdirectories
	 * @return
	 */
	private int getFileCount() {
		BaseStorage.instance.database.refreshObject(this);
		return (this.files != null ? this.files.size() : 0) + (this.children != null ? this.children.stream().mapToInt(EntityArchive::getFileCount).sum() : 0);
	}

	@Override
	public int getTotalFileCount() {
		return this.parent != null ? this.parent.getTotalFileCount() : this.getFileCount();
	}

	public EntityWorkspace getWorkspace() {
		return this.parent != null ? this.parent.getWorkspace() : this.workspace;
	}

	public void setSubmissionArchive(EntityWorkspace workspace) {
		this.workspace = workspace;
		this.parent = null;
	}
}
