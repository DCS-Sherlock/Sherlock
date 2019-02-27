package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.model.detection.LegecyDetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

/**
 * Class to hold the data for a single item in FileReport.
 */
public class FileReportItem {
	private LegecyDetectionType legecyDetectionType;
	private float score;
	private List<? extends ICodeBlock> codeBlocks;
	private List<ITuple<Integer, Integer>> lineNumbers;
	private String description;

	public FileReportItem(LegecyDetectionType legecyDetectionType, float score, List<? extends ICodeBlock> codeBlocks, List<ITuple<Integer, Integer>> lineNumbers, String description) {
		this.legecyDetectionType = legecyDetectionType;
		this.score = score;
		this.codeBlocks = codeBlocks;
		this.lineNumbers = lineNumbers;
		this.description = description;
	}

	public LegecyDetectionType getType() {
		return legecyDetectionType;
	}

	public float getScore() {
		return score;
	}

	public List<? extends ICodeBlock> getCodeBlocks() {
		return codeBlocks;
	}

	public List<ITuple<Integer, Integer>> getLineNumbers() {
		return lineNumbers;
	}

	public String getDescription() {
		return description;
	}

}
