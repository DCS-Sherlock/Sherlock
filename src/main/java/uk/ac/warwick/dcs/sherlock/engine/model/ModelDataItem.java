package uk.ac.warwick.dcs.sherlock.engine.model;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;

import java.util.*;

public class ModelDataItem implements IModelDataItem {

	private ISourceFile file;
	private Map<String, List<IndexedString>> mapping;

	public ModelDataItem(ISourceFile file) {
		this.file = file;
		this.mapping = new HashMap<>();
	}

	public ModelDataItem(ISourceFile file, Map<String, List<IndexedString>> map) {
		this.file = file;
		this.mapping = new HashMap<>(map);
	}

	@Override
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
