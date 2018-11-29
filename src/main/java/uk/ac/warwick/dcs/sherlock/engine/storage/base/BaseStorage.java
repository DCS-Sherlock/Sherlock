package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.io.FilenameUtils;
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
		if (!this.filesystem.storeFile(file, fileContent)) {
			return;
		}

		this.database.storeFile(file);
	}
}
