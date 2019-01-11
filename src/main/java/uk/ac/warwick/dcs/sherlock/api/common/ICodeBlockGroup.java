package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;

import java.util.*;

/**
 * An interface that connects multiple ICodeBlocks where plagiarism is detected between those files.
 */
public interface ICodeBlockGroup {

	/**
	 * Adds a code block to the group
	 * @param file File containing the block
	 * @param score score (o to 1) of the block within the group, eg: 1 means block exactly matches the other blocks in the group
	 * @param startLineNumber first line of the block, [inclusive]
	 * @param endLineNumber last line of the block, [inclusive]
	 */
	void addCodeBlock(ISourceFile file, float score, int startLineNumber, int endLineNumber);

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
