package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import java.util.*;

/**
 * Stored by SubmissionMatch to ensure data for a given file remains together.
 */
public class SubmissionMatchItem {
	/**
	 * The file this item belongs to
	 */
	private ISourceFile file;

	/**
	 * The score for this file, for the given block
	 */
	private float score;

	/**
	 * The line numbers in this file where the match is
	 */
	private List<ITuple<Integer, Integer>> lineNumbers;

	/**
	 * Initialise a new SubmissionMatchItem.
	 * @param file The file the match was found in
	 * @param score The score assigned to this match
	 * @param lineNumbers The location of the match in the file
	 */
	public SubmissionMatchItem(ISourceFile file, float score, List<ITuple<Integer, Integer>> lineNumbers) {
		this.file = file;
		this.score = score;
		this.lineNumbers = lineNumbers;
	}

	/**
	 * @return the ISourceFile this item bleongs to
	 */
	public ISourceFile GetFile() {
		return this.file;
	}

	/**
	 * @return the score for this file
	 */
	public float GetScore() {
		return this.score;
	}

	/**
	 * @return the line numbers the match was found in
	 */
	public List<ITuple<Integer, Integer>> GetLineNumbers() {
		return this.lineNumbers;
	}
}
