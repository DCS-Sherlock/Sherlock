package uk.ac.warwick.dcs.sherlock.engine.lib;

public class Reference {

	public static final String version = "@VERSION@";
	public static boolean isDevelEnv = version.replace("@", "").equals("VERSION");

	public enum Side {UNKNOWN, CLIENT, SERVER}
}
