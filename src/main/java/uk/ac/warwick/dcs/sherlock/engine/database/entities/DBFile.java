package uk.ac.warwick.dcs.sherlock.engine.database.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

// just a test, reimplement with @Table
@Entity
public class DBFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String extension;

	public DBFile() {
	}

	public DBFile(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return this.extension;
	}

	public Long getId() {
		return id;
	}
}
