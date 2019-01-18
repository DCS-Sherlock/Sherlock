package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

public class ReportGenerator extends AbstractReportGenerator {

	ReportGenerator() {
		super();
	}

	@Override
	public FileReport GenerateReport(long persistentId, List<? extends ICodeBlockGroup> codeBlockGroups) {
		FileReport fileReport = new FileReport(persistentId);

		StringJoiner stringJoiner = new StringJoiner("", "", ".");

		/**
		 * for each codeBlockGroup:
		 * get base description
		 * get continuation of description as many times as needed
		 * fill in blanks: line numbers, files to start with
		 * combine strings into one string, add to report
		 */
		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			//Get the base description for this type of plagiarism
			List<String> descriptionSegments = new ArrayList<>();
			descriptionSegments.add(baseDescriptions.get(codeBlockGroup.getDetectionType()));

			//If the group has more than 2 blocks, the description must be extended.
			for (int i = 2; i < codeBlockGroup.getCodeBlocks().size(); i++) {
				descriptionSegments.add(ReportDescriptions.getContinuedDescription());
			}

			/*
			 * Get the line numbers for these code blocks and format the description using them
			 * NB with this basic approach, requires the base descriptions to keep line numbers in consistent order.
			 * Only the first tuple is taken from getLineNumbers.
			 */
			List<ITuple<Integer, Integer>> lineNumbers = new ArrayList<>();
			for (ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
				lineNumbers.add(codeBlock.getLineNumbers().get(0));
			}

			//Format each segment of the description with the appropriate line numbers and add to the StringJoiner along the way.
			//TODO: other formatting including file names, variables, etc.
			for (int i = 0; i < codeBlockGroup.getCodeBlocks().size(); i++) {
				String formattedString = String.format(descriptionSegments.get(i), lineNumbers.get(i).getKey(), lineNumbers.get(i).getValue());
				stringJoiner.add(formattedString);
			}

			//Get the joined string and add it to the report.
			String joinedString = stringJoiner.toString();
			fileReport.AddReportString(joinedString);
		}

		return fileReport;
	}
}
