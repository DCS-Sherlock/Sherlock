package uk.ac.warwick.dcs.sherlock.engine.component;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

public interface IResultTask {
	ITask getTask();

	void setTaskScore(float score);

	float getTaskScore();

	void addFileScore(ISourceFile file, float score);

	Map<ISourceFile, Float> getFileScores();

	List<ICodeBlockGroup> getContainingBlocks();

}
