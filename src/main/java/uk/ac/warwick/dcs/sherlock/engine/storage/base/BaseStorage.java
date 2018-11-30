package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.io.FilenameUtils;
import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.entities.DBFile;

import java.sql.Timestamp;
import java.util.*;

public class BaseStorage implements IStorageWrapper {

	private EmbeddedDatabase database;
	private FilesystemStorage filesystem;

	public BaseStorage() {
		this.database = new EmbeddedDatabase();
		this.filesystem = new FilesystemStorage();

		//Do a scan of all files in database in background, check they are there and not tampered with
		List<DBFile> orphans = this.filesystem.validateFileStore(this.database.getAllFiles());
		if (orphans != null) {
			this.database.removeFiles(orphans);
		}
	}

	@Override
	public void close() {
		this.database.close();
	}

	@Override
	public void storeFile(String filename, byte[] fileContent) {
		DBFile file = new DBFile(this.database.temporaryStudent(), FilenameUtils.getBaseName(filename), FilenameUtils.getExtension(filename), new Timestamp(System.currentTimeMillis()));
		if (!this.filesystem.storeFile(file, fileContent)) {
			return;
		}

		this.database.storeFile(file);

		this.filesystem.loadFile(file);
	}
}
