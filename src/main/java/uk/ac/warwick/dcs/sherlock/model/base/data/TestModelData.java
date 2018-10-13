package uk.ac.warwick.dcs.sherlock.model.base.data;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IModelData;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TestModelData implements IModelData {

	private List<ISourceFile> files;

	//TODO: temporary, migrate to real data structure
	private Map<ISourceFile, Stream<String>> temporaryMapping;

	public TestModelData(List<ISourceFile> files) {
		this.files = files;
		this.temporaryMapping = new HashMap<>();
	}

	public void addPreProcessedFileData(Class<? extends IPreProcessor> proc, ISourceFile file, Stream<String> data) {
		this.temporaryMapping.put(file, data);
	}

	@Override
	public List<ISourceFile> getFileList() {
		return this.files;
	}

	@Override
	public Stream<String> getPreProcessedFileData(Class<? extends IPreProcessor> proc, ISourceFile file) {
		return this.temporaryMapping.get(file);
	}
}
