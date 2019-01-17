package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.io.Serializable;
import java.util.*;

public class EntityCodeBlock implements ICodeBlock, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public float getBlockScore() {
		return 0;
	}

	@Override
	public ISourceFile getFile() {
		return null;
	}

	@Override
	public List<ITuple<Integer, Integer>> getLineNumbers() {
		return null;
	}
}
