package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;

import java.util.List;
import java.util.stream.Stream;

public interface IModelData {

	List<ISourceFile> getFileList();

	Stream<String> getPreProcessedFileData(Class<? extends IPreProcessor> proc, ISourceFile file);

}
