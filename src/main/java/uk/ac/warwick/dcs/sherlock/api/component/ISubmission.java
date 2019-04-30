package uk.ac.warwick.dcs.sherlock.api.component;

import java.util.*;

/**
 * Interface for accessing data from a single submission, this contains many files and might contain many child submission, which represent directories within the submission
 */
public interface ISubmission extends Comparable<ISubmission> {

	/**
	 * submission equality check
	 *
	 * @param o submission to check against
	 *
	 * @return is submission equal
	 */
	boolean equals(ISubmission o);

	/**
	 * Returns a list of all files, across all levels of this submission
	 *
	 * @return total list of files
	 */
	List<ISourceFile> getAllFiles();

	/**
	 * Returns a list of sub-directories on this level of the submission structure
	 *
	 * @return the list of immediate sub-directories
	 */
	List<ISubmission> getContainedDirectories();

	/**
	 * Returns a list of files on this level of the submission structure
	 *
	 * @return the list of files in this directory
	 */
	List<ISourceFile> getContainedFiles();

	/**
	 * Calculates the file count of this directory and all subdirectories
	 *
	 * @return count
	 */
	int getFileCount();

	/**
	 * Fetches the submission unique id
	 *
	 * @return the unique id
	 */
	long getId();

	/**
	 * The name of the submission, should uniquely identify the submission content
	 *
	 * @return String containing the name
	 */
	String getName();

	/**
	 * Get the parent, return null if no parent exists
	 *
	 * @return parent or null if there isn't one
	 */
	ISubmission getParent();

	/**
	 * Returns the total count of all files in this submission
	 *
	 * @return the total file count
	 */
	int getTotalFileCount();

	/**
	 * Does this instance have a parent submission. If true this instance is a containedDirectory of the parent
	 *
	 * @return has a parent
	 */
	boolean hasParent();

	/**
	 * Remove the submission from the database, cannot be recovered.
	 */
	void remove();

}
