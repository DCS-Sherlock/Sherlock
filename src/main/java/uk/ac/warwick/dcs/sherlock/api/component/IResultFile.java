package uk.ac.warwick.dcs.sherlock.api.component;

import java.util.*;

public interface IResultFile {

	void addFileScore(ISourceFile file, float score);

	IResultTask addTaskResult(ITask task);

	ISourceFile getFile();

	float getFileScore(ISourceFile file);

	Map<ISourceFile, Float> getFileScores();

	float getOverallScore();

	void setOverallScore(float score);

	List<IResultTask> getTaskResults();

}
