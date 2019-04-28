package uk.ac.warwick.dcs.sherlock.api.component;

import java.util.*;

/**
 * Stores the results of an individual file for an individual task (detector), is used as child of {@link IResultFile}
 */
public interface IResultTask {

	void addContainingBlock(ICodeBlockGroup blockGroup);

	void addContainingBlock(Collection<ICodeBlockGroup> blockGroups);

	void addFileScore(ISourceFile file, float score);

	List<ICodeBlockGroup> getContainingBlocks();

	float getFileScore(ISourceFile file);

	Map<ISourceFile, Float> getFileScores();

	ITask getTask();

	float getTaskScore();

	void setTaskScore(float score);

}
