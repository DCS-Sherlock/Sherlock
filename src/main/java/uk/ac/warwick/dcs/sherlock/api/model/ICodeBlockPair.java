package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.*;

/**
 * An interface that connects two ICodeBlocks where plagiarism is detected between two files.
 * <p>
 * This and ICodeBlockPair are somewhat redundant with the stuff in IModelProcessedResults but are easier to work with for the ReportGenerator stuff for the time being.
 */
public interface ICodeBlockPair {

	/**
	 * @return the two blocks of code that were flagged as similar
	 */
	List<? extends ICodeBlock> getCodeBlocks();

	/**
	 * TODO It might make more sense if this returns a list of DetectionTypes in case multiple kinds of plagiarism are detected.
	 *
	 * @return the type of plagiarism that was detected for these blocks of code
	 */
	DetectionType getDetectionType();
}
