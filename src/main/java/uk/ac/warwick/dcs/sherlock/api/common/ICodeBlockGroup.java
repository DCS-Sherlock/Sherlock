package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

/**
 * An interface that connects multiple ICodeBlocks where plagiarism is detected between those files.
 */
public interface ICodeBlockGroup {

	/**
	 * Adds a code block to the group
	 * @param file File containing the block
	 * @param score score (0 to 1) of the block within the group, eg: 1 means block exactly matches the other blocks in the group
	 * @param line Tuple containing the start and end line of the code block
	 */
	void addCodeBlock(ISourceFile file, float score, ITuple<Integer, Integer> line);

	/**
	 * Adds a code block to the group
	 * @param file File containing the block
	 * @param score score (0 to 1) of the block within the group, eg: 1 means block exactly matches the other blocks in the group
	 * @param lines list of tuples, each containing the start and end line of the code block, the block covers multiple groups of non-consecutive lines in this file
	 */
	void addCodeBlock(ISourceFile file, float score, List<ITuple<Integer, Integer>> lines);

	/**
	 * @return the blocks of code that were flagged as similar
	 */
	List<? extends ICodeBlock> getCodeBlocks();

	/**
	 * TODO It might make more sense if this returns a list of DetectionTypes in case multiple kinds of plagiarism are TODO detected. Not sure; also depends on how the DetectionTypes are decided on by the algs in the first place.
	 *
	 * @return the the type of plagiarism that was detected for these blocks of code
	 */
	DetectionType getDetectionType();

	/**
	 * @return extra string comment or detail regarding this code block
	 */
	String getComment();
}
