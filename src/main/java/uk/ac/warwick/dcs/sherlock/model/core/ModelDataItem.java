package uk.ac.warwick.dcs.sherlock.model.core;

import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelDataItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelDataItem implements IModelDataItem {

	private ISourceFile file;
	private Map<String, List<IndexedString>> mapping;

	public ModelDataItem(ISourceFile file) {
		this.file = file;
		this.mapping = new HashMap<>();
	}

	public void addPreProcessedLines(String strategyName, List<IndexedString> lines) {
		this.mapping.put(strategyName, lines);
	}

	@Override
	public ISourceFile getFile() {
		return this.file;
	}

	@Override
	public List<IndexedString> getPreProcessedLines(String strategyName) {
		return this.mapping.get(strategyName);
	}
}
