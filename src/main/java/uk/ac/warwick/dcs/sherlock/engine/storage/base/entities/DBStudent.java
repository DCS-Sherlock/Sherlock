package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity (name = "Student")
public class DBStudent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue //(strategy = GenerationType.IDENTITY)
	private long id;

	private String identifier;

	@OneToMany (mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<DBFile> files = new ArrayList<>();

	public DBStudent() {
		super();
	}

	public DBStudent(String identifier) {
		super();
		this.identifier = identifier;
	}

	public List<DBFile> getFiles() {
		return files;
	}

	public String getIdentifier() {
		return this.identifier;
	}
}
