package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.io.Serializable;
import java.util.*;

public class EntityCodeBlockGroup implements ICodeBlockGroup, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void addCodeBlock(ISourceFile file, float score, ITuple<Integer, Integer> line) {

	}

	@Override
	public void addCodeBlock(ISourceFile file, float score, List<ITuple<Integer, Integer>> lines) {

	}

	@Override
	public List<? extends ICodeBlock> getCodeBlocks() {
		return null;
	}

	@Override
	public DetectionType getDetectionType() {
		return null;
	}

	@Override
	public String getComment() {
		return null;
	}
}
