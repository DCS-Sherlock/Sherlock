package uk.ac.warwick.dcs.sherlock.engine.core;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

public class SherlockConfiguration {

	private int randomint;
	private String data_path;

	public SherlockConfiguration() {
		this.randomint = 5;
		this.setData_Path(SystemUtils.IS_OS_WINDOWS ? System.getenv("APPDATA") + File.separator + "Sherlock" : System.getProperty("user.home") + File.separator + ".Sherlock");
	}

	public String getData_Path() {
		return data_path;
	}

	public void setData_Path(String database_path) {
		this.data_path = database_path.replace("/", File.separator).replace("\\", File.separator).replaceAll(File.separator + "$", "");
	}

	public int getRandomint() {
		return randomint;
	}

	public void setRandomint(int randomint) {
		this.randomint = randomint;
	}
}
