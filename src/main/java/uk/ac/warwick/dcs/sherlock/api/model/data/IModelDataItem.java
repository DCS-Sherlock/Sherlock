package uk.ac.warwick.dcs.sherlock.api.model.data;

import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;

import java.util.*;

public interface IModelDataItem {

	/**
	 * Adds a set of preprocessed lines, output from an {@link uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy}, to the dataset
	 *
	 * @param strategyName string name of the {@link uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy}
	 * @param lines        the set of indexed preprocessing lines
	 */
	void addPreProcessedLines(String strategyName, List<IndexedString> lines);

	/**
	 * @return the file which the lines in this data item originate from
	 */
	ISourceFile getFile();

	/**
	 * Retrieves the set of preprcoessed lines from an {@link uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy}
	 *
	 * @param strategyName string name of the {@link uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy}
	 *
	 * @return the set of indexed preprocessing lines
	 */
	List<IndexedString> getPreProcessedLines(String strategyName);

}
