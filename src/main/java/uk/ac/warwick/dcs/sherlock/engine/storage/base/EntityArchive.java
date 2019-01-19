package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Archive")
public class EntityArchive implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	private String filename;

	@ManyToOne (fetch = FetchType.LAZY)
	private EntityArchive parent;

	@OneToMany (mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityArchive> children;

	@OneToMany (mappedBy = "archive", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityFile> files;

	public EntityArchive() {
		super();
	}

	public EntityArchive(String filename) {
		this(filename, null);
	}

	public EntityArchive(String filename, EntityArchive archive) {
		super();
		this.filename = filename;
		this.parent = archive;
		this.children = new LinkedList<>();
	}

	public void addChild(EntityArchive archive) {
		this.children.add(archive);
	}

	public List<EntityArchive> getChildren() {
		BaseStorage.instance.database.refreshObject(this);
		return this.children;
	}

	public String getFilename() {
		return filename;
	}

	public List<EntityFile> getFiles() {
		BaseStorage.instance.database.refreshObject(this);
		return this.files;
	}

	public long getId() {
		return this.id;
	}

	public EntityArchive getParent() {
		return this.parent;
	}
}
