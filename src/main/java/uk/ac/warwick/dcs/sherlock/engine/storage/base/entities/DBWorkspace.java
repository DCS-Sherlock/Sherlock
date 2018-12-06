package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Workspace")
public class DBWorkspace implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	private String name;

	@OneToMany (mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<DBFile> files = new ArrayList<>();

	public DBWorkspace() {
		super();
	}

	public DBWorkspace(String name) {
		super();
		this.name = name;
	}

	public List<DBFile> getFiles() {
		return this.files;
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

}
