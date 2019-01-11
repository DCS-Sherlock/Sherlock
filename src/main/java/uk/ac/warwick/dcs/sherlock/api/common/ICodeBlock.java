package uk.ac.warwick.dcs.sherlock.api.common;

import java.util.*;

/**
 * Interface for storing a single block of code flagged by a detector as suspected plagiarism.
 */
public interface ICodeBlock {

	/*
	 * @return the file containing the block
	 */
	ISourceFile getFile();

	/**
	 * @return the score (0 to 1) of this block within the group
	 *
	 * Example: 1 - all of this block matches the group
	 */
	float getBlockScore();

	/**
	 * TODO This could also be altered to show specific line numbers as e.g. variable renaming might take place over many TODO separate lines.
	 *
	 * @return a list of 2 line numbers that define the start and end of the code block (inclusive)
	 */
	@Deprecated
	List<Integer> getLineNumbers();
}
