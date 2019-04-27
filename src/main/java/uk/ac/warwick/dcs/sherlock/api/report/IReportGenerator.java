package uk.ac.warwick.dcs.sherlock.api.report;

import uk.ac.warwick.dcs.sherlock.api.component.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

/**
 * Implementations of this interface are used by the Report Manager to generate reports for files where plagiarism is suspected (though it can be run for any file in principle).
 */
public interface IReportGenerator<T extends ISubmissionMatchGroup> {

	/**
	 * Compare two submissions and find all the instances of detected plagiarism between all files within them.
	 *
	 * @param submissions     The submissions to compare (should be a list of two submissions only; any submissions beyond the first two are ignored)
	 * @param codeBlockGroups The ICodeBlockGroups that contain at least one file from either submission.
	 *
	 * @return a list of SubmissionMatchGroup objects, which each contain a list of SubmissionMatches and a score for the corresponding IResultTask.
	 */
	List<T> generateSubmissionComparison(List<ISubmission> submissions, List<? extends ICodeBlockGroup> codeBlockGroups);

	/**
	 * Generate a report for a single submission, containing all matches for all files within it, and creating a summary in the process.
	 *
	 * @param submission      The submission to generate the report for.
	 * @param codeBlockGroups The ICodeBlockGroups that contain at least one file from the submission.
	 * @param subScore        The overall score for this submission.
	 *
	 * @return a tuple containing a list of SubmissionMatchGroup objects, which each contain a list of SubmissionMatches and a score for the corresponding IResultTask. The tuple also contains a string
	 * which serves as a summary of the report.
	 */
	ITuple<List<T>, String> generateSubmissionReport(ISubmission submission, List<? extends ICodeBlockGroup> codeBlockGroups, float subScore);

}
