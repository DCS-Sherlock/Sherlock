package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.report.ISubmissionSummary;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

public class SubmissionSummary implements ISubmissionSummary {

	/**
	 * The persistent id of the submission
	 */
	private long persistentId;

	/**
	 * The score of this submission, which is either:
	 * - the overall score if this object is stored in the key of the results map
	 * - the relative score if this object is stored in the value of the results map
	 */
	private float score;

	/**
	 * A list of matching submissions; each item is a tuple containing that submission's id and the score between them.
	 */
	private List<ITuple<Long, Float>> matchingSubmissions;

	/**
	 * Initialises the SubmissionSummary object.
	 *
	 * @param persistentId the persistent ID of this submissions
	 * @param score the overall score for this submission
	 */
	public SubmissionSummary(long persistentId, float score) {
		this.persistentId = persistentId;
		this.score = score;
		matchingSubmissions = new ArrayList<>();
	}

	/**
	 * Adds new submissions to the matchingSubmissions list that this submission had plagiarism detected between.
	 * @param matches a list of the submission ids and the scores between that submission and this one.
	 */
	public void AddMatchingSubmissions(List<? extends ITuple<Long, Float>> matches) {
		matchingSubmissions.addAll(matches);
	}

	/**
	 * Retrieves the submission id.
	 *
	 * @return the persistent id of this submission.
	 */
	@Override
	public long getPersistentId() {
		return persistentId;
	}

	/**
	 * Retrieves the score.
	 *
	 * @return the overall score for this submission.
	 */
	@Override
	public float getScore() {
		return score;
	}

	/**
	 * Retrieves the matching submission list.
	 *
	 * @return the list of matching submissions and their relative scores.
	 */
	@Override
	public List<ITuple<Long, Float>> getMatchingSubmissions() {
		return matchingSubmissions;
	}
}
