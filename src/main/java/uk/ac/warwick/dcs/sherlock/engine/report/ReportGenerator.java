package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator extends AbstractReportGenerator {

	ReportGenerator() { super(); }

	@Override
	public FileReport GenerateReport(long persistentId, List<? extends ICodeBlockGroup> codeBlockGroups) {
		FileReport fileReport = new FileReport(persistentId);

		/**
		 * for each codeBlockGroup:
		 * get base description
		 * get continuation of description as many times as needed
		 * fill in blanks: line numbers, files to start with
		 * combine strings into one string, add to report
		 */
		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			//Get the base description for this type of plagiarism
			String currentDescription = baseDescriptions.get(codeBlockGroup.getDetectionType());

			List<String> continuedDescription = new ArrayList<>();

			/*
			 * Get the line numbers for these code blocks and format the description using them
			 * NB with this basic approach, requires the base descriptions to keep line numbers in consistent order.
			 * Only the first tuple is taken from getLineNumbers.
			 */
			List<ITuple<Integer, Integer>> lineNumbers = new ArrayList<>();
			for (ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
				lineNumbers.add(codeBlock.getLineNumbers().get(0));
			}

			//If the group has more than 2 blocks, the description must be extended.
			for(int i = 2; i < codeBlockGroup.getCodeBlocks().size(); i++) {
				continuedDescription.add(ReportDescriptions.getContinuedDescription());
			}

			//Kind of gross but not sure if string.format can just take a list
			//Note that any files in the CodeBlockGroup beyond the first 2 are ignored
			String lineNumberDesc = String.format(currentDescription, lineNumbers.get(0), lineNumbers.get(1), lineNumbers.get(2), lineNumbers.get(3));

			fileReport.AddReportString(lineNumberDesc);
		}

		return fileReport;
	}
}
