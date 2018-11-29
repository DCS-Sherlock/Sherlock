package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

// just a test, reimplement with @Table
@Entity
@Table(name="files")
public class DBFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String filename;
	private String extension;

	private Timestamp timestamp;
	private String hash;
	private byte[] secure;

	public DBFile() {
	}

	public DBFile(String filename, String extension, Timestamp timestamp) {
		this.filename = filename;
		this.extension = extension;
		this.timestamp = timestamp;
		this.hash = null;
		this.secure = null;
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

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Long getId() {
		return id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public byte[] getSecureParam() {
		return secure;
	}

	public void setSecureParam(byte[] secure) {
		this.secure = secure;
	}
}
