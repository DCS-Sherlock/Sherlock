package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;

import java.util.*;

/**
 * This is based on the old ICodeBlockPair rather than ICodeBlockGroup, and is generally outdated, but may be useful for reference as a first pass at generating reports.
 * <p>
 * rough plan/overview of the process: - have file with descriptions of problems - take a given file pair - go through each CodeBlock in CodeBlockPair - Generate string by taking problem description
 * according to the DetectionType enum of the block - Replace string generic parts with relevant line numbers etc. - Make larger string joining together all of the reasons along the way - done???
 */
public class BasicReportGenerator extends AbstractReportGenerator {

	BasicReportGenerator() {
		super();
	}

	@Override
	public FileReport GenerateReport(long persistentId, List<? extends ICodeBlockGroup> codeBlockGroups) {
		FileReport report = new FileReport(persistentId);

		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			//Get the base description for this type of plagiarism
			String currentDescription = baseDescriptions.get(codeBlockGroup.getDetectionType());

			/*
			 * Get the line numbers for these code blocks and format the description using them
			 * NB with this basic approach, requires the base descriptions to keep line numbers in consistent order.
			 * Only the first tuple is taken from getLineNumbers.
			 */
			List<Integer> lineNumbers = new ArrayList<>();
			for (ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
				int firstLine = codeBlock.getLineNumbers().get(0).getKey();
				int lastLine = codeBlock.getLineNumbers().get(0).getValue();
				lineNumbers.add(firstLine);
				lineNumbers.add(lastLine);
			}

			//Kind of gross but not sure if string.format can just take a list
			//Note that any files in the CodeBlockGroup beyond the first 2 are ignored
			String lineNumberDesc = String.format(currentDescription, lineNumbers.get(0), lineNumbers.get(1), lineNumbers.get(2), lineNumbers.get(3));

			report.AddReportString(lineNumberDesc);
		}

		return report;
	}
}
