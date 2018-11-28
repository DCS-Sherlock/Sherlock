package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;

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
}
