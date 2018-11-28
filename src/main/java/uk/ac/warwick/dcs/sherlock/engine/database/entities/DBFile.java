package uk.ac.warwick.dcs.sherlock.engine.database.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

// just a test, reimplement with @Table
@Entity
public class DBFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String filename;
	private String extension;

	@Temporal (TemporalType.TIMESTAMP)
	private java.util.Date timestamp;
	private String hash;

	public DBFile() {
	}

	public DBFile(String filename, String extension, Date timestamp, String hash) {
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

	public Date getTimestamp() {
		return timestamp;
	}
}
