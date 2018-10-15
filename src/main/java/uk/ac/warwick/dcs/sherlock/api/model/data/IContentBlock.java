package uk.ac.warwick.dcs.sherlock.api.model.data;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;

public interface IContentBlock {

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
		return new GenericContentBlock(file, startLine, endLine, -1, -1);
	}

	/**
	 * Constructs a default IContentBlock based on the API internal implementation
	 *
	 * @param file              File containing code block
	 * @param startLine         Line number of start of block
	 * @param endLine           Line number of end of block
	 * @param startCharacterPos Position of the first character in the block on the start line
	 * @param endCharacterPos   Position of the final character in the block on the end line
	 *
	 * @return configured instance of IContentBlock
	 */
	static IContentBlock of(ISourceFile file, int startLine, int endLine, int startCharacterPos, int endCharacterPos) {
		return new GenericContentBlock(file, startLine, endLine, startCharacterPos, endCharacterPos);
	}

	int getEndCharacterPos();

	/**
	 * @return Line number of end of block
	 */
	int getEndLine();

	/**
	 * @return File containing code block
	 */
	ISourceFile getFile();

	int getStartCharacterPos();

	/**
	 * @return Line number of start of block
	 */
	int getStartLine();

	class GenericContentBlock implements IContentBlock {

		private ISourceFile file;
		private int start, end, startChar, endChar;

		private GenericContentBlock(ISourceFile file, int startLine, int endLine, int startCharacterPos, int endCharacterPos) {
			this.file = file;
			this.start = startLine;
			this.end = endLine;
			this.startChar = startCharacterPos;
			this.endChar = endCharacterPos;
		}

		@Override
		public int getEndCharacterPos() {
			return this.endChar;
		}

		@Override
		public int getEndLine() {
			return this.end;
		}

		@Override
		public ISourceFile getFile() {
			return this.file;
		}

		@Override
		public int getStartCharacterPos() {
			return this.startChar;
		}

		@Override
		public int getStartLine() {
			return this.start;
		}

		@Override
		public String toString() {
			return String.format("(%s [%d, %d])", this.file.getFilename(), this.start, this.end);
		}
	}
}
