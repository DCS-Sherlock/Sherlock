package uk.ac.warwick.dcs.sherlock.engine.report;

import java.util.*;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

/**
 * Object to be sent to web report pages, detailing a particular match between files in different submissions.
 */
public class SubmissionMatch {
	/**
	 * The description of the type of plagiarism for this match (from DetectionType)
	 */
	private String reason;

	/**
	 * The contents of the matches; each has an ISourceFile, a score, and line numbers.
	 */
	private List<SubmissionMatchItem> items;

	/**
	 * Initialise a new SubmissionMatch object.
	 * @param reason description of plagiarism type
	 * @param items SubmissionMatchItems to populate this object with (see SubmissionMatchItem constructor)
	 */
	public SubmissionMatch(String reason, List<SubmissionMatchItem> items) {
		this.reason = reason;
		this.items = items;
	}

	/**
	 * @return the description for this match
	 */
	public String getReason() {
		return this.reason;
	}


	/**
	 * @return a list of SubmissionMatchItems, each containing an ISourceFile, a score, and a set of line numbers.
	 */
	public List<SubmissionMatchItem> getItems() {
		return this.items;
	}
}
