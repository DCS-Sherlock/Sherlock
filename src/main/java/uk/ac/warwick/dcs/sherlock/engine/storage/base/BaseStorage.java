package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.entities.DBFile;

import java.sql.Timestamp;

public class BaseStorage implements IStorageWrapper {

	private EmbeddedDatabase database;
	private FilesystemStorage filesystem;

	public BaseStorage() {
		this.database = new EmbeddedDatabase();
		this.filesystem = new FilesystemStorage();

		//Do a scan of all files in database in background, check they are there and not tampered with
	}

	@Override
	public void close() {
		this.database.close();
	}

	@Override
	public void storeFile(String filename, byte[] fileContent) {
		System.out.println(filename);
		DBFile file = new DBFile(FilenameUtils.getBaseName(filename), FilenameUtils.getExtension(filename), new Timestamp(System.currentTimeMillis()));
		String hash = this.filesystem.storeFile(this.computeFileIdentifier(file), fileContent, "");
		file.setHash(hash);
		this.database.storeFile(file);
	}

	private String computeFileIdentifier(DBFile file) {
		String str = file.getFilename() + file.getExtension() + file.getTimestamp().getTime();
		str = StringUtils.rightPad(str, 30, str);
		return DigestUtils.sha1Hex(str.substring(0, 30));
	}
}
