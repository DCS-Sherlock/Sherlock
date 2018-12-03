package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class DBStudent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;

	private String identifier;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<DBFile> files = new ArrayList<>();

	public DBStudent() {
		super();
		this.identifier = "temporary";
	}

	public void addFile(DBFile file) {
		this.files.add(file);
	}

	public void removeFile(DBFile file) {
		this.files.remove(file);
	}

}
