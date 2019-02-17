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
	}

	public List<EntityArchive> getChildren() {
		BaseStorage.instance.database.refreshObject(this);
		return this.children;
	}

	@Override
	public List<ISubmission> getContainedDirectories() {
		return new LinkedList<>(this.getChildren());
	}

	@Override
	public List<ISourceFile> getContainedFiles() {
		return new LinkedList<>(this.getFiles());
	}

	public String getName() {
		return name;
	}

	public List<EntityFile> getFiles() {
		BaseStorage.instance.database.refreshObject(this);
		return this.files;
	}

	@Override
	public long getId() {
		return this.parent == null ? this.id : this.parent.getId();
	}

	public EntityArchive getParent() {
		return this.parent;
	}

	@Override
	public int getTotalFileCount() {
		return (this.files != null ? this.files.size() : 0) + (this.children != null ? this.children.stream().mapToInt(EntityArchive::getTotalFileCount).sum() : 0);
	}
}
