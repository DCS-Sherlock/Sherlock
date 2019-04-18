package uk.ac.warwick.dcs.sherlock.engine.report;

import java.util.List;

/**
 * Object to store SubmissionMatches, organised by the IResultTasks they come from (i.e. one SubmissionMatchGroup should have all relevant SubmissionMatch objects from one IResultTask)
 */
public class SubmissionMatchGroup {
	/**
	 * The SubmissionMatch objects are what contain the actual important information
	 */
	private List<SubmissionMatch> matches;

	/**
	 * The overall score for this IResultTask.
	 */
	private float groupScore;

	/**
	 * Create a new SubmissionMatchGroup
	 * @param matches A list of all SubmissionMatch objects that were generated based off of a single IResultTask.
	 * @param score the overall score of the IResultTask.
	 */
	public SubmissionMatchGroup(List<SubmissionMatch> matches, float score) {
		this.matches = matches;
		this.groupScore = score;
	}

	/**
	 * Get the matches
	 * @return The stored list of SubmissionMatch obejcts.
	 */
	public List<SubmissionMatch> GetMatches() {
		return this.matches;
	}

	/**
	 * Get the score
	 * @return The overall score for this group.
	 */
	public float GetScore() {
		return this.groupScore;
	}
}
