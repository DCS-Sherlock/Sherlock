package uk.ac.warwick.dcs.sherlock.api.report;

import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

public interface ISubmissionSummary {

	/**
	 * Retrieves the submission id.
	 *
	 * @return the persistent id of this submission.
	 */
	long getPersistentId();

	/**
	 * Retrieves the score.
	 *
	 * @return the overall score for this submission.
	 */
	float getScore();

	/**
	 * Retrieves the matching submission list.
	 *
	 * @return the list of matching submissions and their relative scores.
	 */
	List<ITuple<Long, Float>> getMatchingSubmissions();
}
