package uk.ac.warwick.dcs.sherlock.engine.report;

import java.util.List;

/*
for merging you could put all of the ICodeBlockGroups from a task into 1 "mega" match, and use the file task score for that match


Maybe the best way would be to break the report down into tasks first, then allow for lower level viewing:

- initially show a set of grouped matches for each task or DetectionType (I would need to generate a weighted score for the DetectionTypes for each file, easy to do tho).

- the user can then chose a single one to expand and show each individial match
 */
public class SubmissionMatchGroup {
	private List<SubmissionMatch> matches;
	private float groupScore;

	public SubmissionMatchGroup(List<SubmissionMatch> matches, float score) {
		this.matches = matches;
		this.groupScore = score;
	}

	public List<SubmissionMatch> GetMatches() {
		return this.matches;
	}

	public float GetScore() {
		return this.groupScore;
	}
}
