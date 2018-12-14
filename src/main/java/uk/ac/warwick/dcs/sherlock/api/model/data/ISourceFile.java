package uk.ac.warwick.dcs.sherlock.api.model.data;

import java.io.InputStream;

public interface ISourceFile {

	InputStream getFileContents();

	String getFilename();

	/**
	 * @return fetches a unique, persistent id for the file. No other file should EVER be able to take this ID, even if this file is deleted.
	 */
	long getPersistentId();
}
