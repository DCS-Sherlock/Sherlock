package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.*;

/**
 * Interface for storing a single block of code flagged by a detector as suspected plagiarism.
 */
public interface ICodeBlock {
	/**
	 * TODO This could also be altered to show specific line numbers as e.g. variable renaming might take place over many
	 * TODO separate lines.
	 *
	 * @return a list of 2 line numbers that define the start and end of the code block (inclusive)
	 */
	List<Integer> getLineNumbers();

	/**
	 * TODO  Could be substituted for the ID for the file or whatever is most convenient
	 *
	 * @return the name of the file this code block was found in
	 */
	String getFileName();
}
