package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;

import java.util.*;

/**
 * Implementations of this interface are used by the Report Manager to generate reports for files where plagiarism is suspected (though it can be run for any file in principle).
 */
public interface IReportGenerator {

	/**
	 * Generate a report for a single file.
	 *
	 * @param sourceFile    The file that the report will be generated for.
	 * @param codeBlockGroups The ICodeBlockGroups that contain ICodeBlocks from the file whose report will be generated. Supplied by the Report Manager.
	 *
	 * @return The report itself.
	 */
	public FileReport GenerateReport(ISourceFile sourceFile, List<? extends ICodeBlockGroup> codeBlockGroups);

	/**
	 * Compare two submissions and find all the instances of detected plagiarism between all files within them.
	 *
	 * @param submissions The submissions to compare (should be a list of two submissions only; any submissions beyond the first two are ignored)
	 * @param codeBlockGroups The ICodeBlockGroups that contain at least one ICodeBlock from a file from each submission. Supplied by the Report Manager.
	 * @return a list of SubmissionMatch objects, which each contain the ids of the files involved, the score for that match, a reason from DetectionType, and line numbers where the match occurs.
	 */
	public List<SubmissionMatch> GenerateSubmissionComparison(List<ISubmission> submissions, List<? extends ICodeBlockGroup> codeBlockGroups);
}
