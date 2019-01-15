package uk.ac.warwick.dcs.sherlock.api.util;

/**
 * Sherlock running on a local client, a server. Use UNKNOWN when it does not matter which side
 */
public enum Side {UNKNOWN, CLIENT, SERVER;

	public boolean valid(Side sideToCompare) {
		return this.equals(Side.UNKNOWN) || this.equals(sideToCompare);
	}}
