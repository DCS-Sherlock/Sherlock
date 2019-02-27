package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.model.detection.LegecyDetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

public class ReportGenerator implements IReportGenerator {

	ReportGenerator() {
	}

	@Override
	public FileReport GenerateReport(ISourceFile sourceFile, List<? extends ICodeBlockGroup> codeBlockGroups, List<String> variableNames) {
		FileReport fileReport = new FileReport(sourceFile.getPersistentId());

		StringJoiner stringJoiner = new StringJoiner("", "", ".");

		//Each codeBlockGroup represents a different similarity; each gets its own string added to the report.
		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {

			//New code to get decection type, not connected to rest of this method so the code still compiles
			DetectionType detectionType = null;
			try {
				detectionType = codeBlockGroup.getDetectionType();
			}
			catch (UnknownDetectionTypeException e) {
				e.printStackTrace();
			}

			LegecyDetectionType legacyDetectionType = null;

			//Get the base description for this type of plagiarism
			List<String> descriptionSegments = new ArrayList<>();
			descriptionSegments.add(ReportDescriptions.getLocationDescription(legacyDetectionType, false));

			//Extend the description for the number of files in the group.
			for (int i = 1; i < codeBlockGroup.getCodeBlocks().size(); i++) {
				descriptionSegments.add(ReportDescriptions.getLocationDescription(legacyDetectionType, true));
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
				if (legacyDetectionType == legacyDetectionType.IDENTIFIER) {
					formattedString = String.format(descriptionSegments.get(i), fileNames.get(i), lineNumbers.get(i).getKey());
				}
				else {
					formattedString = String.format(descriptionSegments.get(i), fileNames.get(i), lineNumbers.get(i).getKey(), lineNumbers.get(i).getValue());
				}
				stringJoiner.add(formattedString);
			}

			//Now add the actual description content according to the type of plagiarism in this CodeBlockGroup.
			try {
				stringJoiner.add(codeBlockGroup.getDetectionType().getReason()); // NEW
			}
			catch (UnknownDetectionTypeException e) {
				e.printStackTrace();
			}

			//Get the joined string and add it to the report.
			String joinedString = stringJoiner.toString();

			//Assemble the information into a FileReportItem and add it to the report.
			float score = codeBlockGroup.getCodeBlock(sourceFile).getBlockScore();
			FileReportItem reportItem = new FileReportItem(legacyDetectionType, score, codeBlockGroup.getCodeBlocks(), lineNumbers, joinedString);
			fileReport.AddReportItem(reportItem);
		}

		return fileReport;
	}
}
