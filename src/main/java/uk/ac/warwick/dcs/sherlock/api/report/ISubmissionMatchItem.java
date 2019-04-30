package uk.ac.warwick.dcs.sherlock.api.report;

import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

public interface ISubmissionMatchItem {

	/**
	 * @return the ISourceFile this item bleongs to
	 */
	ISourceFile getFile();

	/**
	 * @return the line numbers the match was found in
	 */
	List<ITuple<Integer, Integer>> getLineNumbers();

	/**
	 * @return the score for this file
	 */
	float getScore();

}
