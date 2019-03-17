package uk.ac.warwick.dcs.sherlock.engine.report;

import java.util.*;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

public class SubmissionMatch {
	/**
	 * The id for the first file in this match
	 */
	private long fileId1;

	/**
	 * The id for the second file in this match
	 */
	private long fileId2;

	/**
	 * The score assigned to this match
	 */
	private float matchScore;

	/**
	 * The description of the type of plagiarism for this match (from DetectionType)
	 */
	private String reason;

	/**
	 * The line numbers in the first file where this match was found
	 */
	private List<ITuple<Integer, Integer>> lineNumbers1;

	/**
	 * The line numbers in the second file where this match was found
	 */
	private List<ITuple<Integer, Integer>> lineNumbers2;

	/**
	 * Initialise a new SubmissionMatch object. Make sure the file ids and line numbers correspond to each other.
	 * @param fileId1 id of first file
	 * @param fileId2 id of second file
	 * @param score score for this match
	 * @param reason description of plagiarism type
	 * @param lineNumbers1 line numbers in the first file
	 * @param lineNumbers2 line numbers in the second file
	 */
	public SubmissionMatch(long fileId1, long fileId2, float score, String reason,
						   List<ITuple<Integer, Integer>> lineNumbers1, List<ITuple<Integer, Integer>> lineNumbers2) {
		this.fileId1 = fileId1;
		this.fileId2 = fileId2;
		this.matchScore = score;
		this.reason = reason;
		this.lineNumbers1 = lineNumbers1;
		this.lineNumbers2 = lineNumbers2;
	}

	/**
	 * @return the persistent id of the first file
	 */
	public long getFileId1() {
		return fileId1;
	}

	/**
	 * @return the persistent id of the second file
	 */
	public long getFileId2() {
		return fileId2;
	}

	/**
	 * @return the score for this match
	 */
	public float getScore() {
		return matchScore;
	}

	/**
	 * @return the description for this match
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @return the line numbers for the first file
	 */
	public List<ITuple<Integer, Integer>> getLineNumbers1() {
		return lineNumbers1;
	}

	/**
	 * @return the line numbers for the second file
	 */
	public List<ITuple<Integer, Integer>> getLineNumbers2() {
		return lineNumbers2;
	}
}
