package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultTask;

import java.util.*;

/**
 * Implementations of this interface are used by the Report Manager to generate reports for files where plagiarism is suspected (though it can be run for any file in principle).
 */
public interface IReportGenerator {
	/**
	 * Compare two submissions and find all the instances of detected plagiarism between all files within them.
	 *
	 * @param submissions The submissions to compare (should be a list of two submissions only; any submissions beyond the first two are ignored)
	 * @param resultTasks The IResultTasks where for each task, at least one ICodeBlockGroup contains either of the two submission's ids.
	 * @return a list of SubmissionMatchGroup objects, which each contain a list of SubmissionMatches and a score for the corresponding IResultTask.
	 */
	public List<SubmissionMatchGroup> GenerateSubmissionComparison(List<ISubmission> submissions, List<? extends IResultTask> resultTasks);

	/**
	 * Generate a report for a single submission, containing all matches for all files within it, and creating a summary in the process.
	 *
	 * @param submission The submission to generate the report for.
	 * @param resultTasks The IResultTasks where for each task, at least one ICodeBlockGroup contains submission's id.
	 * @param subScore The overall score for this submission.
	 * @return a tuple containing a list of SubmissionMatchGroup objects, which each contain a list of SubmissionMatches and a score for the corresponding IResultTask. The tuple also contains a string which serves as a summary of the report.
	 */
	public ITuple<List<SubmissionMatchGroup>, String> GenerateSubmissionReport(ISubmission submission, List<? extends IResultTask> resultTasks, float subScore);

}
