package uk.ac.warwick.dcs.sherlock.api.util;

public enum Side {
	UNKNOWN, CLIENT, SERVER;

	public boolean valid(Side sideToCompare) {
		return this.equals(Side.UNKNOWN) || this.equals(sideToCompare);
	}
}
