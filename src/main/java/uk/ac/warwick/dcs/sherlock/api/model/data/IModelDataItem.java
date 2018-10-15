package uk.ac.warwick.dcs.sherlock.api.model.data;

import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessorBase;

import java.util.List;

public interface IModelDataItem {

	void addPreProcessedLines(Class<? extends IPreProcessorBase> proc, List<IndexedString> lines);

	ISourceFile getFile();

	List<IndexedString> getPreProcessedLines(Class<? extends IPreProcessorBase> proc);

}
