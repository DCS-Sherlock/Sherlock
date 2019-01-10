package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

/**
 * A Tuple of Integer and String, is typically used to associate a string with a file line number within the Sherlock API and Engine
 */
public class IndexedString extends Tuple<Integer, String> {

	public IndexedString(int index, String string) {
		super(index, string);
	}

	public static IndexedString of(int index, String string) {
		return new IndexedString(index, string);
	}

}
