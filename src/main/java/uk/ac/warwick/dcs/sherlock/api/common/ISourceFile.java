package uk.ac.warwick.dcs.sherlock.api.common;

import java.io.InputStream;
import java.util.*;

public interface ISourceFile {

	/**
	 * File equality check
	 *
	 * @param file file to compare
	 *
	 * @return equals
	 */
	boolean equals(ISourceFile file);

	/**
	 * @return the content of the file
	 */
	InputStream getFileContents();

	/**
	 * @return the content of the file as a string
	 */
	String getFileContentsAsString();

	/**
	 * @return the content of the file as a list of strings
	 */
	List<String> getFileContentsAsStringList();

	/**
	 * @return a web path safe file identifier
	 */
	String getFileIdentifier();

	/**
	 * @return string containing display formatted file name
	 */
	String getFileDisplayName();

	/**
	 * @return string containing display formatted file path
	 */
	String getFileDisplayPath();

	/**
	 * @return fetches a unique, persistent id for the file. No other file should EVER be able to take this ID, even if this file is deleted.
	 */
	long getPersistentId();

	/**
	 * @return
	 */
	long getSubmissionId();
}
