package uk.ac.warwick.dcs.sherlock.api.component;

import java.util.*;

/**
 * Object which stores the set of results from a job execution, organised by file.
 */
public interface IResultJob {

	/**
	 * Adds a file to the result set
	 * @param file File to add to results
	 * @return results instance for the file passed
	 */
	IResultFile addFile(ISourceFile file);

	/**
	 * Returns the list of {@link IResultFile} stored for this job
	 * @return list of results
	 */
	List<IResultFile> getFileResults();

	/**
	 * The unique id for the job result
	 *
	 * @return the unique id
	 */
	long getPersistentId();

	/**
	 * Remove this instance, and any children, from storage
	 */
	void remove();

	/**
	 * Write this instance, and its children, to storage
	 */
	void store();

}
