package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;

import java.util.*;

public class ReportGenerator implements IReportGenerator {

	ReportGenerator() {
	}

	@Override
	public FileReport GenerateReport(ISourceFile sourceFile, List<? extends ICodeBlockGroup> codeBlockGroups) {
		FileReport fileReport = new FileReport(sourceFile.getPersistentId());

		StringJoiner stringJoiner = new StringJoiner("; ", "", ".");

		//Each codeBlockGroup represents a different similarity; each gets its own string added to the report.
		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {

			//Get detection type
			DetectionType detectionType = null;
			try {
				detectionType = codeBlockGroup.getDetectionType();
			}
			catch (UnknownDetectionTypeException e) {
				e.printStackTrace();
			}

			//Get the base description for this type of plagiarism; it is the first part of the description for this group.
			stringJoiner.add(detectionType.getReason());

			//Store a list of file names and line numbers to format the description strings with.
			//TODO: Ensure distinct file names (add persistent ID?)
			List<String> fileNames = new ArrayList<>();
			List<List<ITuple<Integer, Integer>>> lineNumbers = new ArrayList<>();
			for (ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
				lineNumbers.add(codeBlock.getLineNumbers());
				fileNames.add(codeBlock.getFile().getFileDisplayName());
			}

			String filename_string = "File %s: ";
			//Extend the description for the number of files in the group, and format each string along the way.
			for (int i = 0; i < codeBlockGroup.getCodeBlocks().size(); i++) {
				String formatted_filename = String.format(filename_string, fileNames.get(i));

				String formatted_lines = "";
				for(int j = 0; j < lineNumbers.get(i).size(); j++) {
					//If this isn't the first set of lines, separate with a comma
					String current_segment = "";
					if(j > 0) {
						current_segment += ", ";
					}

					//If the code block line numbers are the same, there's no need to say "line 45 to line 45"
					if (lineNumbers.get(i).get(j).getKey() == lineNumbers.get(i).get(j).getValue()) {
						current_segment += "line %2$d";
						formatted_lines = String.format(current_segment, lineNumbers.get(i).get(j).getKey());
					} else {
						current_segment += "lines %2$d to %3$d";
						formatted_lines = String.format(current_segment, lineNumbers.get(i).get(j).getKey(), lineNumbers.get(i).get(j).getValue());
					}
				}
				String formatted_final = formatted_filename + formatted_lines;
				stringJoiner.add(formatted_final);
			}

			//Get the joined string and add it to the report.
			String joinedString = stringJoiner.toString();

			//Assemble the information into a FileReportItem and add it to the report.
			float score = codeBlockGroup.getCodeBlock(sourceFile).getBlockScore();
			FileReportItem reportItem = new FileReportItem(detectionType.getIdentifier(), score, codeBlockGroup.getCodeBlocks(), joinedString);
			fileReport.AddReportItem(reportItem);
		}

		return fileReport;
	}

	@Override
	public List<SubmissionMatch> GenerateSubmissionComparison(List<ISubmission> submissions, List<? extends ICodeBlockGroup> codeBlockGroups) {
		List<SubmissionMatch> matches = new ArrayList<>();
		//Create a SubmissionMatch object for each CodeBlockGroup
		for(ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			long fileId1 = -1, fileId2 = -1;
			float score = 1f;	//TODO: get actual score
			String reason = "";
			List<ITuple<Integer, Integer>> lineNumbers1 = new ArrayList<>(), lineNumbers2 = new ArrayList<>();

			//Try to get the detection type and use it to get the string reason
			DetectionType detectionType = null;
			try {
				detectionType = codeBlockGroup.getDetectionType();
			}
			catch (UnknownDetectionTypeException e) {
				e.printStackTrace();
			}

			try {
				reason = detectionType.getReason();
			}
			catch(NullPointerException e) {
				e.printStackTrace();
			}

			//get the file ids and line numbers
			for(ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
				if(submissions.get(0).getId() == codeBlock.getFile().getSubmissionId()) {
					fileId1 = codeBlock.getFile().getPersistentId();
					lineNumbers1 = codeBlock.getLineNumbers();
				}
				else if (submissions.get(1).getId() == codeBlock.getFile().getSubmissionId()) {
					fileId2 = codeBlock.getFile().getPersistentId();
					lineNumbers2 = codeBlock.getLineNumbers();
				}
			}

			//Add the SubmissionMatch to the list
			SubmissionMatch match = new SubmissionMatch(fileId1, fileId2, score, reason, lineNumbers1, lineNumbers2);
			matches.add(match);
		}

		return matches;
	}
}
