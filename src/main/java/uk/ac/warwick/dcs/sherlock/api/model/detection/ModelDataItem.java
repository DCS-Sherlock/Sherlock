package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.IndexedString;

import java.util.*;

public class ModelDataItem {

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

	public void addPreProcessedLines(String strategyName, List<IndexedString> lines) {
		this.mapping.put(strategyName, lines);
	}

	public ISourceFile getFile() {
		return this.file;
	}

	public List<IndexedString> getPreProcessedLines(String strategyName) {
		return this.mapping.get(strategyName);
	}
}
