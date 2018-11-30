package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
public class DBFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="FILE_ID")
	private long id;

	@ManyToOne
	@JoinColumn(name = "STUDENT_ID")
	private DBStudent student;

	private String filename;
	private String extension;

	private Timestamp timestamp;
	private String hash;
	private byte[] secure;

	public DBFile() {
	}

	public DBFile(DBStudent student, String filename, String extension, Timestamp timestamp) {
		this.student = student;
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
}
