package uk.ac.warwick.dcs.sherlock.engine;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

public class Configuration {

	private String dataPath;
	private Boolean encryptFiles;

	public Configuration() {
		this.setDataPath(SystemUtils.IS_OS_WINDOWS ? System.getenv("APPDATA") + File.separator + "Sherlock" : System.getProperty("user.home") + File.separator + ".Sherlock");
		this.setEncryptFiles(true);
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String data_path) {
		this.dataPath = data_path.replace("/", File.separator).replace("\\", File.separator).replaceAll(File.separator + "$", "");
	}

	public Boolean getEncryptFiles() {
		return encryptFiles;
	}

	public void setEncryptFiles(Boolean encryptFiles) {
		this.encryptFiles = encryptFiles;
	}
}
