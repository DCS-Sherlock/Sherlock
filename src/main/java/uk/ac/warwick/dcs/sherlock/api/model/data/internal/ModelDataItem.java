package uk.ac.warwick.dcs.sherlock.api.model.data.internal;

import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessorBase;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelDataItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelDataItem implements IModelDataItem {

	private ISourceFile file;
	private Map<Class<? extends IPreProcessorBase>, List<IndexedString>> mapping;

	public ModelDataItem(ISourceFile file) {
		this.file = file;
		this.mapping = new HashMap<>();
	}

	public void addPreProcessedLines(Class<? extends IPreProcessorBase> proc, List<IndexedString> lines) {
		this.mapping.put(proc, lines);
	}

	@Override
	public ISourceFile getFile() {
		return this.file;
	}

	@Override
	public List<IndexedString> getPreProcessedLines(Class<? extends IPreProcessorBase> proc) {
		return this.mapping.get(proc);
	}
}
