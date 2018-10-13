package uk.ac.warwick.dcs.sherlock.lib;

public class Reference
{
    public static final String version = "@VERSION@";
	public static boolean isDevelEnv = version.replace("@", "") == "VERSION";
}
