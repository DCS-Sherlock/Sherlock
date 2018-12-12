package uk.ac.warwick.dcs.sherlock.module.model.base;

import uk.ac.warwick.dcs.sherlock.api.model.AbstractReportGenerator;
import uk.ac.warwick.dcs.sherlock.api.model.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.model.ICodeBlockPair;
import uk.ac.warwick.dcs.sherlock.api.model.DetectionType;

import java.util.*;

/**
 * A fairly basic first pass to generate the presentable reports shown to the user.
 * This uses ICodeBlockPair but could be adapted to the stuff in ModelProcessedRessults; I thought it would be better
 * to have the report stuff separate for the time being though.
 *
 * rough plan/overview of the process:
 * - have file with descriptions of problems
 * - take a given file pair
 * - go through each CodeBlock in CodeBlockPair
 * - Generate string by taking problem description according to the DetectionType enum of the block
 * - Replace string generic parts with relevant line numbers etc.
 * - Make larger string joining together all of the reasons along the way
 * - done???
 */
public class BasicReportGenerator extends AbstractReportGenerator {

	/**
	 * The unformatted descriptions are stored in this map of strings
	 *
	 * TODO: actually getting those descriptions
	 */
	private Map<DetectionType, String> baseDescriptions;

	@Override
	public String GenerateReport(List<? extends ICodeBlockPair> codeBlockPairs) {
		StringJoiner stringJoiner = new StringJoiner("\n");

		for (ICodeBlockPair codeBlockPair : codeBlockPairs) {
			//Get the base description for this type of plagiarism
			String currentDescription = baseDescriptions.get(codeBlockPair.getDetectionType());

			//Get the line numbers for these code blocks and format the description using them
			//NB with this basic approach, requires the base descriptions to keep line numbers in consistent order
			List<Integer> lineNumbers = new ArrayList<Integer>();
			for(ICodeBlock codeBlock : codeBlockPair.getCodeBlocks()) {
				lineNumbers.addAll(codeBlock.getLineNumbers());
			}

			//Kind of gross but not sure if string.format can just take a list 
			String lineNumberDesc = String.format(currentDescription, lineNumbers.get(0), lineNumbers.get(1), lineNumbers.get(2), lineNumbers.get(3));

			//TODO: further formatting with e.g. variable names etc.

			stringJoiner.add(lineNumberDesc);
		}

		//Final formatting
		String outputReport = stringJoiner.toString();

		return outputReport;
	}
}
