package uk.ac.warwick.dcs.sherlock.api.report;

import java.util.*;

public interface ISubmissionMatch<T extends ISubmissionMatchItem> {

	/**
	 * @return a list of SubmissionMatchItems, each containing an ISourceFile, a score, and a set of line numbers.
	 */
	List<T> getItems();

	/**
	 * @return the description for this match
	 */
	String getReason();

}
