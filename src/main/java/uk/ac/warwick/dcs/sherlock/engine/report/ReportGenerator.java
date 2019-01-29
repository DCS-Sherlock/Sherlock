package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

public class ReportGenerator implements IReportGenerator {

	ReportGenerator() {
	}

	@Override
	public FileReport GenerateReport(long persistentId, List<? extends ICodeBlockGroup> codeBlockGroups,
									 List<String> variableNames) {
		FileReport fileReport = new FileReport(persistentId);

		StringJoiner stringJoiner = new StringJoiner("", "", ".");

		//Each codeBlockGroup represents a different similarity; each gets its own string added to the report.
		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			DetectionType detectionType = codeBlockGroup.getDetectionType();

			//Get the base description for this type of plagiarism
			List<String> descriptionSegments = new ArrayList<>();
			descriptionSegments.add(ReportDescriptions.getLocationDescription(detectionType, false));

			//Extend the description for the number of files in the group.
			for (int i = 1; i < codeBlockGroup.getCodeBlocks().size(); i++) {
				descriptionSegments.add(ReportDescriptions.getLocationDescription(detectionType, true));
			}

			/*
			 * Get the line numbers and file names for these code blocks and format the description using them.
			 * Only the first tuple is taken from getLineNumbers.
			 */
			//TODO: Ensure distinct file names (add persistent ID?)
			List<String> fileNames = new ArrayList<>();
			List<ITuple<Integer, Integer>> lineNumbers = new ArrayList<>();
			for (ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
				lineNumbers.add(codeBlock.getLineNumbers().get(0));
				fileNames.add(codeBlock.getFile().getFileDisplayName());
			}

			//Format each segment of the description with the appropriate file name and line numbers and add to the StringJoiner along the way.
			//TODO: other formatting including variables, score etc.
			for (int i = 0; i < codeBlockGroup.getCodeBlocks().size(); i++) {
				String formattedString;
				if(detectionType == detectionType.IDENTIFIER) {
					formattedString = String.format(descriptionSegments.get(i), fileNames.get(i), lineNumbers.get(i).getKey());
				} else {
					formattedString = String.format(descriptionSegments.get(i), fileNames.get(i), lineNumbers.get(i).getKey(), lineNumbers.get(i).getValue());
				}
				stringJoiner.add(formattedString);
			}

			//Now add the actual description content according to the type of plagiarism in this CodeBlockGroup.
			stringJoiner.add(ReportDescriptions.getDescription(codeBlockGroup.getDetectionType()));

			//Get the joined string and add it to the report.
			String joinedString = stringJoiner.toString();
			fileReport.AddReportString(joinedString);
		}

		return fileReport;
	}
}
