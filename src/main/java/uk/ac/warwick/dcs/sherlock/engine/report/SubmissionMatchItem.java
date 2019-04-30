package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.report.ISubmissionMatchItem;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

/**
 * Stored by SubmissionMatch to ensure data for a given file remains together.
 */
public class SubmissionMatchItem implements ISubmissionMatchItem {

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
	 *
	 * @param file        The file the match was found in
	 * @param score       The score assigned to this match
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
	@Override
	public ISourceFile getFile() {
		return this.file;
	}

	/**
	 * @return the line numbers the match was found in
	 */
	@Override
	public List<ITuple<Integer, Integer>> getLineNumbers() {
		return this.lineNumbers;
	}

	/**
	 * @return the score for this file
	 */
	@Override
	public float getScore() {
		return this.score;
	}
}
