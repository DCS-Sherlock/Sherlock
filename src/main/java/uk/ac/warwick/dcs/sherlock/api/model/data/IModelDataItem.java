package uk.ac.warwick.dcs.sherlock.api.model.data;

import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;

import java.util.List;

public interface IModelDataItem {

	void addPreProcessedLines(String strategyName, List<IndexedString> lines);

	ISourceFile getFile();

	List<IndexedString> getPreProcessedLines(String strategyName);

}
