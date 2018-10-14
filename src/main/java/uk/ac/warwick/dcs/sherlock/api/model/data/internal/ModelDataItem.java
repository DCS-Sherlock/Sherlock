package uk.ac.warwick.dcs.sherlock.api.model.data.internal;

import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelDataItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelDataItem implements IModelDataItem {

	private ISourceFile file;
	private Map<Class<? extends IPreProcessor>, List<IndexedString>> mapping;

	public ModelDataItem(ISourceFile file) {
		this.file = file;
		this.mapping = new HashMap<>();
	}

	public void addPreProcessedLines(Class<? extends IPreProcessor> proc, List<IndexedString> lines) {
		this.mapping.put(proc, lines);
	}

	@Override
	public ISourceFile getFile() {
		return this.file;
	}

	@Override
	public List<IndexedString> getPreProcessedLines(Class<? extends IPreProcessor> proc) {
		return this.mapping.get(proc);
	}
}
