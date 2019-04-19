package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

/**
 * Interface for storing a single block of code flagged by a detector as suspected plagiarism.
 */
public interface ICodeBlock {

	/**
	 * @return the score (0 to 1) of this block within the group
	 * <p>
	 * Example: 1 - all of this block matches the group
	 */
	float getBlockScore();

	/**
	 * @return the file containing the block
	 */
	ISourceFile getFile();

	/**
	 * @return a list of tuples of line numbers that define the start and end of the code block. In most cases the list should be a single item, but if the same block is copied in multiple places in a
	 * file, all instances of the block will be output here.
	 */
	List<ITuple<Integer, Integer>> getLineNumbers();
}
