package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;

import java.util.*;

/**
 * Object to store the preprocessed data passed to the detector
 */
public class ModelDataItem {

	private ISourceFile file;
	private Map<String, List<IndexedString>> mapping;

	/**
	 * Build data item for file
	 *
	 * @param file file to build for
	 */
	public ModelDataItem(ISourceFile file) {
		this.file = file;
		this.mapping = new HashMap<>();
	}

	/**
	 * Build data item for file
	 *
	 * @param file file to build for
	 * @param map  map of indexed lines against the tag for their producing strategy
	 */
	public ModelDataItem(ISourceFile file, Map<String, List<IndexedString>> map) {
		this.file = file;
		this.mapping = new HashMap<>(map);
	}

	/**
	 * Adds a mapping for a preprocessing strategy
	 *
	 * @param strategyName tag for strategy
	 * @param lines        index lines
	 */
	public void addPreProcessedLines(String strategyName, List<IndexedString> lines) {
		this.mapping.put(strategyName, lines);
	}

	/**
	 * Get the file this data item for
	 *
	 * @return file
	 */
	public ISourceFile getFile() {
		return this.file;
	}

	/**
	 * get the preprocessed lines for a strategy, returns null if strategy does not exist
	 *
	 * @param strategyName strategy tag
	 *
	 * @return lines, null if strategy does not exist
	 */
	public List<IndexedString> getPreProcessedLines(String strategyName) {
		return this.mapping.get(strategyName);
	}
}
