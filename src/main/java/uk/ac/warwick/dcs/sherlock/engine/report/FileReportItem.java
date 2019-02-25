package uk.ac.warwick.dcs.sherlock.engine.report;

import java.util.*;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;

/**
 * Class to hold the data for a single item in FileReport.
 */
public class FileReportItem {
	private DetectionType detectionType;
	private float score;
	private List<? extends ICodeBlock> codeBlocks;
	private List<ITuple<Integer, Integer>> lineNumbers;
	private String description;

	public FileReportItem(DetectionType detectionType, float score, List<? extends ICodeBlock> codeBlocks, List<ITuple<Integer, Integer>> lineNumbers, String description) {
		this.detectionType = detectionType;
		this.score = score;
		this.codeBlocks = codeBlocks;
		this.lineNumbers = lineNumbers;
		this.description = description;
	}

	public DetectionType getType() {
		return detectionType;
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
