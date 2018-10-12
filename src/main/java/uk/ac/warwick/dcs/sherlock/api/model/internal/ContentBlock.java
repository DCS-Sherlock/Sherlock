package uk.ac.warwick.dcs.sherlock.api.model.internal;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IContentBlock;

public class ContentBlock implements IContentBlock {

	private ISourceFile file;
	private int start, end, startChar, endChar;

	public ContentBlock(ISourceFile file, int startLine, int endLine, int startCharacterPos, int endCharacterPos) {
		this.file = file;
		this.start = startLine;
		this.end = endLine;
		this.startChar = startCharacterPos;
		this.endChar = endCharacterPos;
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
	public int getEndLine() {
		return this.end;
	}

	@Override
	public int getStartCharacterPos() {
		return this.startChar;
	}

	@Override
	public int getEndCharacterPos() {
		return this.endChar;
	}
}
