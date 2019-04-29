package uk.ac.warwick.dcs.sherlock.api.util;

import java.io.Serializable;

/**
 * Basic tuple implementation
 * @param <K> key type
 * @param <V> value type
 */
public class Tuple<K, V> implements ITuple<K, V>, Serializable {

	private K key;
	private V value;

	public Tuple() {
	}

	public Tuple(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public static <K, V> Tuple of(K key, V value) {
		return new Tuple<>(key, value);
	}

	@Override
	public ITuple clone() {
		return new Tuple<>(this.key, this.value);
	}

	@Override
	public boolean equals(ITuple tuple) {
		return this.key.equals(tuple.getKey()) && this.value.equals(tuple.getValue());
	}

	@Override
	public K getKey() {
		return this.key;
	}

	@Override
	public void setKey(K key) {
		this.key = key;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public boolean keyEquals(ITuple tuple) {
		return this.key.equals(tuple.getKey());
	}

	@Override
	public String toString() {
		return String.valueOf(this.key) + ", " + String.valueOf(this.value);
	}

	@Override
	public boolean valueEquals(ITuple tuple) {
		return this.value.equals(tuple.getValue());
	}
}
