package uk.ac.warwick.dcs.sherlock.api.component;

import java.util.*;

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
