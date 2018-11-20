package uk.ac.warwick.dcs.sherlock.engine;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

public class SherlockConfiguration {

	private int randomint;
	private String database_path;

	SherlockConfiguration() {
		this.randomint = 5;
		this.setDatabase_path(SystemUtils.IS_OS_WINDOWS ? System.getenv("APPDATA") + File.separator + "Sherlock" : System.getProperty("user.home") + File.separator + ".Sherlock");
	}

	public String getDatabase_path() {
		return database_path;
	}

	public void setDatabase_path(String database_path) {
		this.database_path = database_path.replace("/", File.separator).replace("\\", File.separator).replaceAll(File.separator + "$", "");
	}

	public int getRandomint() {
		return randomint;
	}

	public void setRandomint(int randomint) {
		this.randomint = randomint;
	}
}
