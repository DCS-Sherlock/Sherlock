package uk.ac.warwick.dcs.sherlock.api.report;

import uk.ac.warwick.dcs.sherlock.api.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

public interface IReportManager<T extends ISubmissionMatchGroup, S extends ISubmissionSummary> {

	/**
	 * To be called by the web report pages. Gets a list of submission summaries.
	 * @return a list of the matching SubmissionSummaries, each containing their ids, overall scores, and a list of the submissions that they were matched with.
	 */
	List<S> GetMatchingSubmissions();

	/**
	 * Compares two submissions, finds all the matches in files they contain between them, and returns all relevant information about them.
	 *
	 * @param submissions The submissions to compare (should be a list of two submissions only; any submissions beyond the first two are ignored)
	 * @return A list of SubmissionMatchGroup objects which contain lists of SubmissionMatch objects; each have ids of the two matching files, a score for the match, a reason from the DetectionType, and the line numbers in each file where the match occurs.
	 */
	List<T> GetSubmissionComparison(List<ISubmission> submissions);

	/**
	 * Generate a report for a single submission, containing all matches for all files within it, and a summary of the report as a string.
	 *
	 * @param submission The submission to generate the report for.
	 * @return A tuple. The key contains a list of SubmissionMatchGroup objects which contain lists of SubmissionMatch objects; each have objects which contain ids of the two matching files, a score for the match, a reason from the DetectionType, and the line numbers in each file where the match occurs. The value is the report summary.
	 */
	ITuple<List<T>, String> GetSubmissionReport(ISubmission submission);

}
