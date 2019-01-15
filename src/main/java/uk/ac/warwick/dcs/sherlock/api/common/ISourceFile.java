package uk.ac.warwick.dcs.sherlock.api.common;

import java.io.InputStream;

public interface ISourceFile {

	/**
 	 * @return the content of the file
	 */
	InputStream getFileContents();

	/**
	 * @return the content of the file as a string
	 */
	String getFileContentsAsString();

	/**
	 * @return string containing the name of the file to display, this should be a path including any parent archives
	 */
	String getFileDisplayName();

	/**
	 * @return fetches a unique, persistent id for the file. No other file should EVER be able to take this ID, even if this file is deleted.
	 */
	long getPersistentId();
}
