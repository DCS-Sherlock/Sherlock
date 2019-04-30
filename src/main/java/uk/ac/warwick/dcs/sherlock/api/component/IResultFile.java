package uk.ac.warwick.dcs.sherlock.api.component;

import java.util.*;

/**
 * Stores the results of an individual file from a job, is used as child of {@link IResultJob}
 */
public interface IResultFile {

	/**
	 * Adds an aggregate score from all tasks of the instance file against another file
	 *
	 * @param file  file score is against
	 * @param score score value
	 */
	void addFileScore(ISourceFile file, float score);

	/**
	 * Adds a task (an individual detector run) to the file results
	 *
	 * @param task task instance to add
	 *
	 * @return task results instance
	 */
	IResultTask addTaskResult(ITask task);

	/**
	 * Get the file this instance represents
	 *
	 * @return file instance
	 */
	ISourceFile getFile();

	/**
	 * Gets the aggregate score from all tasks of the instance file vs the passed file, the file must have been registered using {@link #addFileScore(ISourceFile, float)}
	 *
	 * @param file file score is against
	 *
	 * @return score value stored
	 */
	float getFileScore(ISourceFile file);

	/**
	 * Returns the full map of stored file vs instance file aggregate score from all tasks
	 *
	 * @return map of scores
	 */
	Map<ISourceFile, Float> getFileScores();

	/**
	 * Gets the overall aggregate score for all tasks and other files for the instance file
	 *
	 * @return score
	 */
	float getOverallScore();

	/**
	 * Sets the overall aggregate score for the file
	 *
	 * @param score aggregate score value
	 */
	void setOverallScore(float score);

	/**
	 * Returns the list of {@link IResultTask} stored for this file
	 *
	 * @return list of tasks
	 */
	List<IResultTask> getTaskResults();

}
