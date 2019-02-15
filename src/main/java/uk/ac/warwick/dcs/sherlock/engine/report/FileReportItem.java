package uk.ac.warwick.dcs.sherlock.engine.report;

import java.util.*;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;

/**
 * Class to hold the data for a single item in FileReport.
 * TODO: multiple line blocks?
 */
public class FileReportItem {
	private DetectionType detectionType;
	private float score;
	private List<ITuple<Integer, Integer>> lineNumbers;
	private String description;

	public FileReportItem(DetectionType detectionType, float score, List<ITuple<Integer, Integer>> lineNumbers, String description) {
		this.detectionType = detectionType;
		this.score = score;
		this.lineNumbers = lineNumbers;
		this.description = description;
	}

	public DetectionType getType() {
		return detectionType;
	}

	public float getScore() {
		return score;
	}

	public List<ITuple<Integer, Integer>> getLineNumbers() {
		return lineNumbers;
	}

	public String getDescription() {
		return description;
	}

}
