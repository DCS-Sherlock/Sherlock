package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.model.detection.LegecyDetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

/**
 * Class to hold the data for a single item in FileReport.
 */
public class FileReportItem {
	private String detectionType;
	private float score;
	private List<? extends ICodeBlock> codeBlocks;
	private String description;

	/**
	 * Create a new FileReportItem object.
	 *
	 * @param detectionTypeIdentifier the identifier name for the detectionType of plagiarism, for the codeBlockGroup this item corresponds to.
	 * @param score the number score for the strength of the plagiarism.
	 * @param codeBlocks a list of the code blocks relevant to this item; can bee used to get the line numbers and file names.
	 * @param description the full string description with the reason and extracted line numbers, file names etc. as generated by the ReportGenerator.
	 */
	public FileReportItem(String detectionTypeIdentifier, float score,
						  List<? extends ICodeBlock> codeBlocks, String description){
		this.detectionType = detectionTypeIdentifier;
		this.score = score;
		this.codeBlocks = codeBlocks;
		this.description = description;
	}

	/**
	 * Get the type of plagiarism for this item.
	 * @return The DetectionType that corresponds to the detectionType identifier string, retrieved from the registry.
	 * @throws UnknownDetectionTypeException if no such DetectionType is found to exist.
	 */
	public DetectionType getDetectionType() throws UnknownDetectionTypeException {
		return SherlockRegistry.getDetectionType(this.detectionType);
	}

	/**
	 * Get the score for this item.
	 * @return a float from 0 to 1 representing how strongly plagiarised this section is.
	 */
	public float getScore() {
		return score;
	}

	/**
	 * Get the relevant code blocks for this item.
	 * @return a list of objects implementing ICodeBlock.
	 */
	public List<? extends ICodeBlock> getCodeBlocks() {
		return codeBlocks;
	}

	/**
	 * Get the full description for this item.
	 * @return A string of the format "[reason]; file X: lines a to b; file Y: lines c to d; ..."
	 */
	public String getDescription() {
		return description;
	}

}
