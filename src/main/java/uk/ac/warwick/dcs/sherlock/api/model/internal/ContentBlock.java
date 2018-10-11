package uk.ac.warwick.dcs.sherlock.api.model.internal;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IContentBlock;

public class ContentBlock implements IContentBlock {

	private ISourceFile file;
	private int start, end;

	public ContentBlock(ISourceFile file, int startLine, int endLine) {
		this.file = file;
		this.start = startLine;
		this.end = endLine;
	}

	@Override
	public ISourceFile getFile() {
		return file;
	}

	@Override
	public int getStartLine() {
		return start;
	}

	@Override
	public int getEndLine() {
		return end;
	}
}
