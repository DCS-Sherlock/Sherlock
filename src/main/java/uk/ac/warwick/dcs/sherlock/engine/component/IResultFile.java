package uk.ac.warwick.dcs.sherlock.engine.component;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

public interface IResultFile {

	boolean addFileScore(ISourceFile file, float score);

	IResultTask addTaskResult(ITask task);

	ISourceFile getFile();

	Map<ISourceFile, Float> getOverallFileScores();

	float getOverallScore();

	void setOverallScore(float score);

	List<IResultTask> getTaskResults();

}
