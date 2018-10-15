package uk.ac.warwick.dcs.sherlock.api.model.data;

import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy;

import java.util.List;

public interface IModelDataItem {

	void addPreProcessedLines(Class<? extends IPreProcessingStrategy> proc, List<IndexedString> lines);

	ISourceFile getFile();

	List<IndexedString> getPreProcessedLines(Class<? extends IPreProcessingStrategy> proc);

}
