package uk.ac.warwick.dcs.sherlock.engine.core;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

public class SherlockConfiguration {

	//private int randomint;
	private String dataPath;

	public SherlockConfiguration() {
		//this.randomint = 5;
		this.setDataPath(SystemUtils.IS_OS_WINDOWS ? System.getenv("APPDATA") + File.separator + "Sherlock" : System.getProperty("user.home") + File.separator + ".Sherlock");
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String data_path) {
		this.dataPath = data_path.replace("/", File.separator).replace("\\", File.separator).replaceAll(File.separator + "$", "");
	}

	/*public int getRandomint() {
		return randomint;
	}

	public void setRandomint(int randomint) {
		this.randomint = randomint;
	}*/
}
