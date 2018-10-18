package uk.ac.warwick.dcs.sherlock.api.util;

public class IndexedString extends Tuple<Integer, String> {

	public IndexedString(int index, String string) {
		super(index, string);
	}

	public static IndexedString of(int index, String string) {
		return new IndexedString(index, string);
	}

}
