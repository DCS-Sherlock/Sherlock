package uk.ac.warwick.dcs.sherlock.api.report;

import java.util.*;

public interface ISubmissionMatchGroup<T extends ISubmissionMatch> {

	/**
	 * Add a SubmissionMatch to the list (used by ReportGenerator)
	 *
	 * @param match the SubmissionMatch to be added
	 */
	void addMatch(T match);

	/**
	 * Get the matches
	 *
	 * @return The stored list of SubmissionMatch objects.
	 */
	public List<T> getMatches();

	/**
	 * Get the descriptor
	 *
	 * @return The type of plagiarism that was detected for these matches.
	 */
	public String getReason();

}
