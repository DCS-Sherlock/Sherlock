package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

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
	 * @param variableNames   A list of all relevant variable names used in the file.
	 *
	 * @return The report itself.
	 */
	public FileReport GenerateReport(ISourceFile sourceFile, List<? extends ICodeBlockGroup> codeBlockGroups, List<String> variableNames);
}
