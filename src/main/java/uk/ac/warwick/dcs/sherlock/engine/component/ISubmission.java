package uk.ac.warwick.dcs.sherlock.engine.component;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

public interface ISubmission extends Comparable<ISubmission> {

	/**
	 * submission equality check
	 * @param o submission to check against
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
	 * Returns the total count of all files in this submission
	 *
	 * @return the total file count
	 */
	int getTotalFileCount();

	/**
	 * Remove the submission from the database, cannot be recovered.
	 */
	void remove();

}
