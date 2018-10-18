package uk.ac.warwick.dcs.sherlock.deprecated;

import java.io.File;
import java.util.*;

public class Settings {

	/**
	 * The directory that holds the source code to be compared.
	 */
	private File sourceDirectory;

	private boolean settingIntitialised = false;

	private boolean preprocessingComplete = false;

	/**
	 * A list of settings selected by the user
	 * <p>
	 * Example:
	 * <p>
	 * [(profile0), (profile1)... (profile2) ] where numbers relate to file_type enums File types that aren't being used are set to false in the profile
	 */
	private List<SettingProfile> settingList = new LinkedList<SettingProfile>();

	/**
	 * Setting Constructor
	 */
	public Settings() {
	}

	public SettingProfile getCommentsProfile() {
		return settingList.get(FileTypes.COM.getValue());
	}

	/**
	 *
	 */
	public List<Boolean> getInUseStatus() {
		List<Boolean> statuses = new LinkedList<Boolean>();
		for (SettingProfile sp : getSettingList()) {
			statuses.add(sp.isInUse());
		}
		return statuses;
	}

	private List<SettingProfile> getSettingList() {
		return settingList;
	}

	public SettingProfile getNoCWSProfile() {
		return settingList.get(FileTypes.NCW.getValue());
	}

	public SettingProfile getNoCommentsProfile() {
		return settingList.get(FileTypes.NOC.getValue());
	}

	public SettingProfile getNoWSProfile() {
		return settingList.get(FileTypes.NWS.getValue());
	}

	public File getOriginalDirectory() {
		String originalDirectory = getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + "Original";
		return new File(originalDirectory);
	}

	public File getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	/**
	 * Returns the Setting profile for the original setting
	 *
	 * @return
	 */
	public SettingProfile getOriginalProfile() {
		return settingList.get(FileTypes.ORI.getValue());
	}

	public SettingProfile getTokenisedProfile() {
		return settingList.get(FileTypes.TOK.getValue());
	}

	public SettingProfile getWSPatternProfile() {
		return settingList.get(FileTypes.WSP.getValue());
	}

	public void initialiseDefault() {
		/* If settings have not been initialised */
		if (!settingIntitialised) {
			settingIntitialised = true;
			for (int setting = 0; setting < FileTypes.getNumberOfFileTypes(); setting++) {
				SettingProfile sp = new SettingProfile(setting, getSourceDirectory(), false);
				settingList.add(sp);
			}
		}
		else {
			settingIntitialised = false;
			resetSettingList();
			initialiseDefault();
		}
	}

	public boolean isPreprocessingComplete() {
		return preprocessingComplete;
	}

	public void loadSettings() {
		for (int setting = 0; setting < FileTypes.getNumberOfFileTypes(); setting++) {
			SettingProfile sp = new SettingProfile(setting, getSourceDirectory(), true);
			settingList.add(sp);
		}
	}

	public void setPreprocessingStatus(boolean status) {
		this.preprocessingComplete = status;
	}

	private void resetSettingList() {
		settingList.clear();
	}

}
