package uk.ac.warwick.dcs.sherlock.engine;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

/**
 * Mapping for the configuration file
 */
public class Configuration {

	private String dataPath;
	private Boolean enableExternalModules;

	private Boolean encryptFiles;
	private int jobCompleteDismissalTime;

	public Configuration() {
		this.setDataPath(SystemUtils.IS_OS_WINDOWS ? System.getenv("APPDATA") + File.separator + "Sherlock" : System.getProperty("user.home") + File.separator + ".Sherlock");
		this.setEnableExternalModules(true);
		this.setEncryptFiles(true);
		this.setJobCompleteDismissalTime(3);
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String data_path) {
		this.dataPath = data_path.replace("/", File.separator).replace("\\", File.separator).replaceAll(File.separator + "$", "");
	}

	public Boolean getEnableExternalModules() {
		return enableExternalModules;
	}

	public void setEnableExternalModules(Boolean enableExternalModules) {
		this.enableExternalModules = enableExternalModules;
	}

	public Boolean getEncryptFiles() {
		return encryptFiles;
	}

	public void setEncryptFiles(Boolean encryptFiles) {
		this.encryptFiles = encryptFiles;
	}

	public int getJobCompleteDismissalTime() {
		return jobCompleteDismissalTime;
	}

	public void setJobCompleteDismissalTime(int jobCompleteDismissalTime) {
		this.jobCompleteDismissalTime = jobCompleteDismissalTime;
	}
}
