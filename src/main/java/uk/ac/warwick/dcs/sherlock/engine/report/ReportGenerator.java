package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;

import java.util.*;

public class ReportGenerator implements IReportGenerator {

	ReportGenerator() {
	}

	@Override
	public List<SubmissionMatch> GenerateSubmissionComparison(List<ISubmission> submissions, List<? extends ICodeBlockGroup> codeBlockGroups) {
		List<SubmissionMatch> matches = new ArrayList<>();
		//Create a SubmissionMatch object for each CodeBlockGroup
		for(ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			String reason = "";
			List<SubmissionMatchItem> items = new ArrayList<>();

			//Try to get the detection type and use it to get the string reason
			DetectionType detectionType = null;
			try {
				detectionType = codeBlockGroup.getDetectionType();
			}
			catch (UnknownDetectionTypeException e) {
				e.printStackTrace();
			}

			if (detectionType != null ) {
				try {
					reason = detectionType.getReason();
				}
				catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

			//Create SubmissionMatchItems for every codeBlock belonging to a file in the submissions being compared.
			for(ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
				Long subId = codeBlock.getFile().getSubmission().getId();
				if(submissions.get(0).getId() == subId || submissions.get(1).getId() == subId) {
					items.add(new SubmissionMatchItem(codeBlock.getFile(), codeBlock.getBlockScore(), codeBlock.getLineNumbers()));
				}

			}

			//Add the SubmissionMatch to the list
			matches.add(new SubmissionMatch(reason, items));
		}

		return matches;
	}

	@Override
	public List<SubmissionMatch> GenerateSubmissionReport(ISubmission submission, List<? extends ICodeBlockGroup> codeBlockGroups) {
		List<SubmissionMatch> matches = new ArrayList<>();
		//Create a SubmissionMatch object for each CodeBlockGroup
		for(ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			String reason = "";
			List<SubmissionMatchItem> items = new ArrayList<>();

			//Try to get the detection type and use it to get the string reason
			DetectionType detectionType = null;
			try {
				detectionType = codeBlockGroup.getDetectionType();
			}
			catch (UnknownDetectionTypeException e) {
				e.printStackTrace();
			}

			if (detectionType != null ) {
				try {
					reason = detectionType.getReason();
				}
				catch (NullPointerException e) {
					e.printStackTrace();
				}
			}


			//Create a SubmissionMatchItem for every CodeBlock
			for(ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
				items.add(new SubmissionMatchItem(codeBlock.getFile(), codeBlock.getBlockScore(), codeBlock.getLineNumbers()));
			}

			//Add the SubmissionMatch to the list
			matches.add(new SubmissionMatch(reason, items));
		}

		return matches;
	}
}
