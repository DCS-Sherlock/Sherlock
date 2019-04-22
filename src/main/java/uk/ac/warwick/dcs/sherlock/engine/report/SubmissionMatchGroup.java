package uk.ac.warwick.dcs.sherlock.engine.report;

import java.util.List;

/**
 * Object to store SubmissionMatches; every SubmissionMatch within one SubmissionMatchGroup should share the same DetectionType.
 */
public class SubmissionMatchGroup {
	/**
	 * The SubmissionMatch objects are what contain the actual important information
	 */
	private List<SubmissionMatch> matches;

	/**
	 * The descriptor for this group of matches.
	 */
	private String reason;

	/**
	 * Create a new SubmissionMatchGroup
	 * @param matches A list of all SubmissionMatch objects that were generated based off of a single IResultTask.
	 * @param reason A string that describes why these matches were flagged for plagiarism.
	 */
	public SubmissionMatchGroup(List<SubmissionMatch> matches, String reason) {
		this.matches = matches;
		this.reason = reason;
	}

	/**
	 * Add a SubmissionMatch to the list (used by ReportGenerator)
	 * @param match the SubmissionMatch to be added
	 */
	public void AddMatch(SubmissionMatch match) {
		this.matches.add(match);
	}

	/**
	 * Get the matches
	 * @return The stored list of SubmissionMatch objects.
	 */
	public List<SubmissionMatch> GetMatches() {
		return this.matches;
	}


	/**
	 * Get the descriptor
	 * @return The type of plagiarism that was detected for these matches.
	 */
	public String GetReason() {
		return this.reason;
	}
}
