package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

/**
 * An interface that connects multiple ICodeBlocks where plagiarism is detected between those files.
 */
public interface ICodeBlockGroup {

	/**
	 * Adds a code block to the group
	 *
	 * @param file  File containing the block
	 * @param score score (0 to 1) of the block within the group, eg: 1 means block exactly matches the other blocks in the group
	 * @param line  Tuple containing the start and end line of the code block
	 */
	void addCodeBlock(ISourceFile file, float score, ITuple<Integer, Integer> line);

	/**
	 * Adds a code block to the group
	 *
	 * @param file  File containing the block
	 * @param score score (0 to 1) of the block within the group, eg: 1 means block exactly matches the other blocks in the group
	 * @param lines list of tuples, each containing the start and end line of the code block, the block covers multiple groups of non-consecutive lines in this file
	 */
	void addCodeBlock(ISourceFile file, float score, List<ITuple<Integer, Integer>> lines);

	/**
	 * Tests whether a passed file is included in the group
	 *
	 * @param file file to test
	 *
	 * @return is the file present?
	 */
	boolean filePresent(ISourceFile file);

	/**
	 * Tests whether any files in the group belong to the specified submission
	 *
	 * @param submissionId the ID of the submission to test
	 *
	 * @return true if there is such a file, false otherwise
	 */
	boolean submissionIdPresent(long submissionId);

	/**
	 * return the block of code for a specific file
	 *
	 * @param file file to search
	 *
	 * @return the files blocks
	 */
	ICodeBlock getCodeBlock(ISourceFile file);

	/**
	 * @return the blocks of code that were flagged as similar
	 */
	List<? extends ICodeBlock> getCodeBlocks();

	/**
	 * @return extra string comment or detail regarding this code block
	 */
	String getComment();

	/**
	 * Set the comment string for this block
	 *
	 * @param comment string
	 */
	void setComment(String comment);

	/**
	 * @return the type of plagiarism that was detected for these blocks of code
	 *
	 * @throws UnknownDetectionTypeException thrown if the stored identifier is not registered in the current session
	 */
	DetectionType getDetectionType() throws UnknownDetectionTypeException;

	/**
	 * Set the detection type for this block
	 *
	 * @param detectionTypeIdentifier detection type
	 *
	 * @throws UnknownDetectionTypeException thrown if the passed identifier is not registered in the current session
	 */
	void setDetectionType(String detectionTypeIdentifier) throws UnknownDetectionTypeException;

	/**
	 * Check whether the group covers at least two files
	 * @return
	 */
	boolean isPopulated();
}
