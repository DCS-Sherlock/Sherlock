package uk.ac.warwick.dcs.sherlock.engine.report;

import java.util.*;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

/**
 * Object to be sent to web report pages, detailing a particular match between files in different submissions.
 */
public class SubmissionMatch {
	/**
	 * The first file in this match
	 */
	private ISourceFile file1;

	/**
	 * The second file in this match
	 */
	private ISourceFile file2;

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
	 * @param file1 first file
	 * @param file2 second file
	 * @param score score for this match
	 * @param reason description of plagiarism type
	 * @param lineNumbers1 line numbers in the first file
	 * @param lineNumbers2 line numbers in the second file
	 */
	public SubmissionMatch(ISourceFile file1, ISourceFile file2, float score, String reason,
						   List<ITuple<Integer, Integer>> lineNumbers1, List<ITuple<Integer, Integer>> lineNumbers2) {
		this.file1 = file1;
		this.file2 = file2;
		this.matchScore = score;
		this.reason = reason;
		this.lineNumbers1 = lineNumbers1;
		this.lineNumbers2 = lineNumbers2;
	}

	/**
	 * @return the first file
	 */
	public ISourceFile getFile1() {
		return file1;
	}

	/**
	 * @return the second file
	 */
	public ISourceFile getFile2() {
		return file2;
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
