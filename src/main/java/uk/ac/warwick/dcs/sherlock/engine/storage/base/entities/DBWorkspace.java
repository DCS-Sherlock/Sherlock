package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class DBWorkspace implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;

	@OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<DBFile> files = new ArrayList<>();

	public DBWorkspace() {
		super();
	}

	public void addFile(DBFile file) {
		this.files.add(file);
	}

	public void removeFile(DBFile file) {
		this.files.remove(file);
	}

}
