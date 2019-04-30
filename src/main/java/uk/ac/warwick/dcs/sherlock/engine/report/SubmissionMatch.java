package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.report.ISubmissionMatch;

import java.util.*;

/**
 * Object to be sent to web report pages, detailing a particular match between files in different submissions.
 */
public class SubmissionMatch implements ISubmissionMatch<SubmissionMatchItem> {
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
	@Override
	public String getReason() {
		return this.reason;
	}


	/**
	 * @return a list of SubmissionMatchItems, each containing an ISourceFile, a score, and a set of line numbers.
	 */
	@Override
	public List<SubmissionMatchItem> getItems() {
		return this.items;
	}
}
