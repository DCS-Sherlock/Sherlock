package uk.ac.warwick.dcs.sherlock.api.util;

/**
 * A Tuple of Integer and String, is typically used to associate a string with a file line number within the Sherlock API and Engine
 */
public class IndexedString extends Tuple<Integer, String> {

	public IndexedString(int index, String string) {
		super(index, string);
	}

	/**
	 * Static constructor method
	 * @param index the index value
	 * @param string the string
	 * @return new instance of IndexedString
	 */
	public static IndexedString of(int index, String string) {
		return new IndexedString(index, string);
	}

}
