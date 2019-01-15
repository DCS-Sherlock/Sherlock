package uk.ac.warwick.dcs.sherlock.api.util;

public interface ITuple<K, V> {

	ITuple clone();

	boolean equals(ITuple tuple);

	K getKey();

	void setKey(K key);

	V getValue();

	void setValue(V value);

	boolean keyEquals(ITuple tuple);

	String toString();

	boolean valueEquals(ITuple tuple);

}
