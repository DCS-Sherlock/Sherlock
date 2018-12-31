package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity (name = "File")
public class EntityFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne (fetch = FetchType.LAZY, optional = false)
	private EntityWorkspace workspace;

	@ManyToOne (fetch = FetchType.LAZY)
	private EntityArchive archive;

	private String filename;
	private String extension;

	private Timestamp timestamp;
	private String hash;
	private byte[] secure;

	public EntityFile() {
		super();
	}

	public EntityFile(String filename, String extension, Timestamp timestamp) {
		this(filename, extension, timestamp, null);
	}

	public EntityFile(String filename, String extension, Timestamp timestamp, EntityArchive archive) {
		super();
		this.filename = filename;
		this.extension = extension;
		this.timestamp = timestamp;
		this.hash = null;
		this.secure = null;
		this.workspace = null;
		this.archive = archive;
	}

	public EntityArchive getArchive() {
		return archive;
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

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public EntityWorkspace getWorkspace() {
		return workspace;
	}

	public void setWorkspace(EntityWorkspace workspace) {
		this.workspace = workspace;
	}
}
