package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.io.Serializable;
import java.util.*;

public class EntityCodeBlock implements ICodeBlock, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public ISourceFile getFile() {
		return null;
	}

	@Override
	public float getBlockScore() {
		return 0;
	}

	@Override
	public List<Integer> getLineNumbers() {
		return null;
	}
}
