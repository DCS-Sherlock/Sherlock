package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.*;

/**
 * An interface that connects two ICodeBlocks where plagiarism is detected between two files.
 *
 * This and ICodeBlockPair are somewhat redundant with the stuff in ModelProcessedResults but are easier to work with for the
 * ReportGenerator stuff for the time being.
 */
public interface ICodeBlockPair {
	/**
	 * @return the two blocks of code that were flagged as similar
	 */
	List<? extends ICodeBlock> getCodeBlocks();

	/**
	 * TODO It might make more sense if this returns a list of DetectionTypes in case multiple kinds of plagiarism are
	 * TODO detected. Not sure; also depends on how the DetectionTypes are decided on by the algs in the first place.
	 *
	 * @return the the type of plagiarism that was detected for these blocks of code
	 */
	DetectionType getDetectionType();

	/**
	 * TODO To implement this there will need to be some file to store descriptions for each kind of plagiarism,
	 * TODO and then an implementation of this function will convert those descriptions into something more complete
	 * TODO using specific parts of the code blocks that are problematic (i.e. filling in the blanks).
	 *
	 * @return a string that is a full written description of why these blocks of code are suspected of plagiarism.
	 */
	String CreateDescription();
}
