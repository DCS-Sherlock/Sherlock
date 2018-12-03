package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
public class DBFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne (fetch = FetchType.LAZY, optional = false)
	private DBStudent student;

	@ManyToOne (fetch = FetchType.LAZY, optional = false)
	private DBWorkspace workspace;

	private String filename;
	private String extension;

	private Timestamp timestamp;
	private String hash;
	private byte[] secure;

	public DBFile() {
		super();
	}

	public DBFile(String filename, String extension, Timestamp timestamp) {
		super();
		this.filename = filename;
		this.extension = extension;
		this.timestamp = timestamp;
		this.hash = null;
		this.secure = null;
		this.student = null;
		this.workspace = null;
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

	public byte[] getSecureParam() {
		return secure;
	}

	public void setSecureParam(byte[] secure) {
		this.secure = secure;
	}

	public DBStudent getStudent() {
		return student;
	}

	public void setStudent(DBStudent student) {
		this.student = student;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public DBWorkspace getWorkspace() {
		return workspace;
	}

	public void setWorkspace(DBWorkspace workspace) {
		this.workspace = workspace;
	}
}
