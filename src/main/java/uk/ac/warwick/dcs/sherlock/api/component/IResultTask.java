package uk.ac.warwick.dcs.sherlock.api.component;

import java.util.*;

/**
 * Stores the results of an individual file for an individual task (detector), is used as child of {@link IResultFile}
 */
public interface IResultTask {

	/**
	 * Adds a code block group found using the tasks detector which contains the file
	 *
	 * @param blockGroup code block group
	 */
	void addContainingBlock(ICodeBlockGroup blockGroup);

	/**
	 * Adds a collection of code block groups found using the tasks detector which all contain the file
	 *
	 * @param blockGroups collection of code block groups
	 */
	void addContainingBlock(Collection<ICodeBlockGroup> blockGroups);

	/**
	 * Adds a score from the task of the instance file against another file
	 *
	 * @param file  file score is against
	 * @param score score value
	 */
	void addFileScore(ISourceFile file, float score);

	/**
	 * Fetches the list of code block groups from the detector which feature the instance file
	 *
	 * @return list of groups
	 */
	List<ICodeBlockGroup> getContainingBlocks();

	/**
	 * Gets the score of the instance file vs the passed file for the task, the file must have been registered using {@link #addFileScore(ISourceFile, float)}
	 *
	 * @param file file score is against
	 *
	 * @return score value stored
	 */
	float getFileScore(ISourceFile file);

	/**
	 * Returns the full map of stored file vs instance file score for the task
	 *
	 * @return map of scores
	 */
	Map<ISourceFile, Float> getFileScores();

	/**
	 * Get the task this objects stores results for
	 * @return Task instance
	 */
	ITask getTask();

	/**
	 * Fetches the overall aggregate score for the instance file for the task
	 * @return score
	 */
	float getTaskScore();

	/**
	 * Sets the overall aggregate score for the instance file for the task
	 * @param score score to set
	 */
	void setTaskScore(float score);

}
