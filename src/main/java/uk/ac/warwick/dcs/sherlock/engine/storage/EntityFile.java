package uk.ac.warwick.dcs.sherlock.engine.storage;

import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.component.IJob;
import uk.ac.warwick.dcs.sherlock.api.component.WorkStatus;
import uk.ac.warwick.dcs.sherlock.engine.storage.BaseStorageFilesystem.IStorable;

import javax.persistence.*;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 * ISourceFile object for base storage implementation
 */
@Entity (name = "File")
public class EntityFile implements ISourceFile, IStorable, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne (fetch = FetchType.LAZY)
	private EntityArchive archive;

	private String filename;
	private String extension;

	private Timestamp timestamp;
	private String hash;
	private byte[] secure;

	private long filesize;
	private int lineCount;
	private int nonEmptyLineCount;

	EntityFile() {
		super();
	}

	public EntityFile(EntityArchive archive, String filename, String extension, Timestamp timestamp, long size, int line, int contentLine) {
		super();
		this.archive = archive;
		this.archive.getFiles_().add(this);

		this.filename = filename;
		this.extension = extension;
		this.timestamp = timestamp;
		this.hash = null;
		this.secure = null;
		this.filesize = size;
		this.lineCount = line;
		this.nonEmptyLineCount = contentLine;
	}

	@Override
	public boolean equals(ISourceFile file) {
		return file.getPersistentId() == this.getPersistentId();
	}

	@Override
	public long getArchiveId() {
		return this.archive.getId();
	}

	public String getDisplayFileSize(boolean si) {
		int unit = si ? 1000 : 1024;
		if (this.filesize < unit) {
			return this.filesize + " B";
		}
		int exp = (int) (Math.log(this.filesize) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", this.filesize / Math.pow(unit, exp), pre);
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
		return this.filename + "." + this.extension;
	}

	@Override
	public String getFileDisplayPath() {
		StringBuilder build = new StringBuilder();
		if (this.archive != null) {
			this.getFileDisplayNameRecurse(build, this.archive, "/");
		}
		build.append(this.filename).append(".").append(this.extension);
		return build.toString();
	}

	@Override
	public String getFileIdentifier() {
		StringBuilder build = new StringBuilder();
		if (this.archive != null) {
			this.getFileDisplayNameRecurse(build, this.archive, "-");
		}
		build.append(this.filename).append(".").append(this.extension);
		return build.toString();
	}

	@Override
	public long getFileSize() {
		return this.filesize;
	}

	@Override
	public String getHash() {
		return this.hash;
	}

	@Override
	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public int getNonEmptyLineCount() {
		return this.nonEmptyLineCount;
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
	public ISubmission getSubmission() {
		EntityArchive sub = this.archive;
		while (sub.hasParent()) {
			sub = sub.getParent_();
		}

		return sub;
	}

	@Override
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	@Override
	public int getTotalLineCount() {
		return this.lineCount;
	}

	@Override
	public void remove() {
		//Set all the jobs as having missing files
		for (IJob job : this.getArchive().getWorkspace().getJobs()) {
			job.setStatus(WorkStatus.MISSING_FILES);
		}

		this.remove_();
		this.archive.clean();
	}

	@Override
	public String toString() {
		return this.getFileDisplayName();
	}

	EntityArchive getArchive() {
		return archive;
	}

	void setArchive(EntityArchive archive) {
		BaseStorage.instance.filesystem.updateFileArchive(this, archive);
		this.archive = archive;
	}

	String getExtension() {
		return this.extension;
	}

	String getFilename() {
		return this.filename;
	}

	void remove_() {
		try {
			BaseStorage.instance.filesystem.removeFile(this);
			BaseStorage.instance.database.removeObject(this);
		}
		catch (Exception ignored) {
		}
	}

	private void getFileDisplayNameRecurse(StringBuilder build, EntityArchive archive, String sep) {
		if (archive.hasParent()) {
			this.getFileDisplayNameRecurse(build, archive.getParent_(), sep);
		}
		build.append(archive.getName()).append(sep);
	}
}
