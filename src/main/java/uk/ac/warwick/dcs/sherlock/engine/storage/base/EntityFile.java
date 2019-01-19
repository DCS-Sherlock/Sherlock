package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.BaseStorageFilesystem.IStorable;

import javax.persistence.*;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Entity (name = "File")
public class EntityFile implements ISourceFile, IStorable, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
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

	public boolean equals(ISourceFile file) {
		return file.getPersistentId() == this.getPersistentId();
	}

	public EntityArchive getArchive() {
		return archive;
	}

	public String getExtension() {
		return this.extension;
	}

	@Override
	public InputStream getFileContents() {
		return BaseStorage.instance.filesystem.loadFile(this);
	}

	@Override
	public String getFileContentsAsString() {
		return BaseStorage.instance.filesystem.loadFileAsString(this);
	}

	@Override
	public List<String> getFileContentsAsStringList() {
		List<String> list = new ArrayList<>();
		Scanner scanner = new Scanner(this.getFileContents());
		while (scanner.hasNextLine()) {
			list.add(scanner.nextLine());
		}
		return list;
	}

	@Override
	public String getFileDisplayName() {
		StringBuilder build = new StringBuilder();
		if (this.archive != null) {
			this.getFileDisplayNameRecurse(build, this.archive);
		}
		build.append(this.filename).append(".").append(this.extension);
		return build.toString();
	}

	public String getFilename() {
		return this.filename;
	}

	@Override
	public String getHash() {
		return this.hash;
	}

	@Override
	public void setHash(String hash) {
		this.hash = hash;
	}

	public long getId() {
		return this.id;
	}

	@Override
	public long getPersistentId() {
		return this.id;
	}

	@Override
	public byte[] getSecureParam() {
		return this.secure;
	}

	@Override
	public void setSecureParam(byte[] secure) {
		this.secure = secure;
	}

	@Override
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public EntityWorkspace getWorkspace() {
		return this.workspace;
	}

	public void setWorkspace(EntityWorkspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public String toString() {
		return this.getFileDisplayName();
	}

	private void getFileDisplayNameRecurse(StringBuilder build, EntityArchive archive) {
		if (archive.getParent() != null) {
			this.getFileDisplayNameRecurse(build, archive.getParent());
		}
		build.append(archive.getFilename()).append("/");
	}
}
