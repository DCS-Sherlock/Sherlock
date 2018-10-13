package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;

public interface IModelData {

	String /* or whatever we decide*/ getPreProcessedFileData(Class<? extends IPreProcessingStrategy> proc, ISourceFile file);

}
