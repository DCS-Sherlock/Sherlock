package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.internal.ContentBlock;

public interface IContentBlock {

	/**
	 * @return File containing code block
	 */
	ISourceFile getFile();

	/**
	 * @return Line number of start of block
	 */
	int getStartLine();

	/**
	 * @return Line number of end of block
	 */
	int getEndLine();

	int getStartCharacterPos();

	int getEndCharacterPos();

	/**
	 * Constructs a default IContentBlock based on the API internal implementation
	 *
	 * @param file      File containing code block
	 * @param startLine Line number of start of block
	 * @param endLine   Line number of end of block
	 *
	 * @return configured instance of IContentBlock
	 */
	static IContentBlock of(ISourceFile file, int startLine, int endLine) {
		return new ContentBlock(file, startLine, endLine, -1, -1);
	}

	/**
	 * Constructs a default IContentBlock based on the API internal implementation
	 *
	 * @param file      File containing code block
	 * @param startLine Line number of start of block
	 * @param endLine   Line number of end of block
	 * @param startCharacterPos Position of the first character in the block on the start line
	 * @param endCharacterPos Position of the final character in the block on the end line
	 *
	 * @return configured instance of IContentBlock
	 */
	static IContentBlock of(ISourceFile file, int startLine, int endLine, int startCharacterPos, int endCharacterPos) {
		return new ContentBlock(file, startLine, endLine, startCharacterPos, endCharacterPos);
	}

}
