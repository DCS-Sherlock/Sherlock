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

	/**
	 * Remove the file
	 */
	void remove();

	/**
	 * Count of the number of lines in the file containing characters, non empty
	 * @return count of non empty lines
	 */
	int getNonEmptyLineCount();

	/**
	 * Count of the total number of lines in the file
	 * @return count of all lines
	 */
	int getTotalLineCount();

	/**
	 * Fetches the file size in bytes
	 * @return file size in bytes
	 */
	long getFileSize();

	/**
	 * Fetches the file size in String form with the correct extension
	 * @param si use SI (1000) or binary (1024) for calculations
	 * @return string for the file size
	 */
	String getDisplayFileSize(boolean si);

}
