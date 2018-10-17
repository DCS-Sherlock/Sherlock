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
		return new GenericContentBlock(file, startLine, endLine);
	}

	/**
	 * @return Line number of end of block
	 */
	int getEndLine();

	/**
	 * @return File containing code block
	 */
	ISourceFile getFile();

	/**
	 * @return Line number of start of block
	 */
	int getStartLine();

	/**
	 * Generic implementation of IContentBlock
	 */
	class GenericContentBlock implements IContentBlock {

		private ISourceFile file;
		private int start, end;

		private GenericContentBlock(ISourceFile file, int startLine, int endLine) {
			this.file = file;
			this.start = startLine;
			this.end = endLine;
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
		public int getStartLine() {
			return this.start;
		}

		@Override
		public String toString() {
			return String.format("(%s [%d, %d])", this.file.getFilename(), this.start, this.end);
		}
	}
}
