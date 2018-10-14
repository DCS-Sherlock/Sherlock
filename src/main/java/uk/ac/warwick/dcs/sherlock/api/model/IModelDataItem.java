package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFileLine;

import java.util.List;

public interface IModelDataItem {

	void addPreProcessedLines(Class<? extends IPreProcessor> proc, List<ISourceFileLine> lines);

	ISourceFile getFile();

	List<ISourceFileLine> getPreProcessedLines(Class<? extends IPreProcessor> proc);

}
