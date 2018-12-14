package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Archive")
public class DBArchive implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue //(strategy = GenerationType.IDENTITY)
	private long id;

	private String filename;

	@ManyToOne (fetch = FetchType.LAZY)
	private DBArchive parent;

	@OneToMany (mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<DBArchive> children;

	@OneToMany (mappedBy = "archive", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<DBFile> files;

	public DBArchive() {
		super();
	}

	public DBArchive(String filename) {
		this(filename, null);
	}

	public DBArchive(String filename, DBArchive archive) {
		super();
		this.filename = filename;
		this.parent = archive;
		this.children = new LinkedList<>();
	}

	public long getId() {
		return this.id;
	}

	public String getFilename() {
		return filename;
	}

	public DBArchive getParent() {
		return this.parent;
	}

	public void addChild(DBArchive archive) {
		this.children.add(archive);
	}

	public List<DBArchive> getChildren() {
		return this.children;
	}

	public List<DBFile> getFiles() {
		return this.files;
	}
}
