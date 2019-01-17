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

	private DetectionType type;
	private String comment;

	EntityCodeBlockGroup() {
		super();
		this.type = null;
		this.comment = null;
	}

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
	public String getComment() {
		return this.comment;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public DetectionType getDetectionType() {
		return this.type;
	}

	@Override
	public void setDetectionType(DetectionType type) {
		this.type = type;
	}
}
