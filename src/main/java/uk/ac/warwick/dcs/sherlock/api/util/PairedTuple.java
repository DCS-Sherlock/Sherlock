package uk.ac.warwick.dcs.sherlock.api.util;

import java.io.Serializable;

public class PairedTuple<W, X, Y, Z> implements Serializable {

	Tuple<W, X> point1;
	Tuple<Y, Z> point2;

	public PairedTuple(W key1, X value1, Y key2, Z value2) {
		this.point1 = new Tuple<>(key1, value1);
		this.point2 = new Tuple<>(key2, value2);
	}

	public Tuple<W, X> getPoint1() {
		return point1;
	}

	public Tuple<Y, Z> getPoint2() {
		return point2;
	}

	@Override
	public String toString() {
		return String.format("[(%s), (%s)]", this.point1.toString(), this.point2.toString());
	}
}
