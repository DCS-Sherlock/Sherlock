package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

// just a test, reimplement with @Table
@Entity
public class DBFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String filename;
	private String extension;

	private Timestamp timestamp;
	private String hash;

	public DBFile() {
	}

	public DBFile(String filename, String extension, Timestamp timestamp, String hash) {
		this.filename = filename;
		this.extension = extension;
		this.timestamp = timestamp;
		this.hash = hash;
	}

	public String getExtension() {
		return this.extension;
	}

	public String getFilename() {
		return filename;
	}

	public String getHash() {
		return hash;
	}

	public Long getId() {
		return id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}
}
