package sherlock.model.analysis.preprocessing;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import sherlock.model.analysis.SettingProfile;

public class Settings {
	public enum SettingChoice{
		/**
		 * Numeric value for the original.
		 */
		ORI(0),
		
		/**
		 * Numeric value for no-whitespace.
		 */
		NWS(1),
		
		/**
		 * Numeric value for no comment.
		 */
		NOC(2),
		
		/**
		 * Numerical value for no comment no white.
		 */
		NCW(3),
		
		/**
 		* Numeric value for comment.
 		*/
		COM(4),
		
		/**
		 * Numeric value for the tokenised files.
		 */
		TOK(5),
		
		/**
		 * Numeric value for the whitespace pattern.
		 */
		WSP(6);
		
		private int value;
		
		SettingChoice(int value) {
			this.value = value;
		}
		
		private int getValue() {
	        return value;
	    }
		
		static int getNumberOfFileTypes() {
			return values().length ;
		}
		
	}
	
	/**
	 * The directory that holds the source code to be compared.
	 */
	private File sourceDirectory;
	
	private boolean settingIntitialised = false ;
	
	/**
	 * A list of settings selected by the user
	 *  
	 *  Example:
	 *  
	 *  [(profile0), (profile1)... (profile2) ]
	 *  where numbers relate to file_type enums 
	 * 	File types that aren't being used are set to false in the profile
	 */
	private List<SettingProfile> settingList = new LinkedList<SettingProfile>();
	
	/**
	 * Setting Constructor
	 */
	public Settings(){
	}
	
	public File getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
		System.out.println("Source Directory: " + sourceDirectory);
	}
	
	public File getOriginalDirectory() {
		String originalDirectory = getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + "Original";
		return new File(originalDirectory);
	}
	
	private List<SettingProfile> getSettingList() {
		return settingList;
	}
	
	private void resetSettingList() {
		settingList.clear();
	}
	

	/**
	 * Returns the Setting profile for the original setting
	 * @return
	 */
	public SettingProfile getOriginalProfile() {
		return settingList.get(Settings.SettingChoice.ORI.getValue());
	}
	
	public SettingProfile getNoWSProfile() {
		return settingList.get(Settings.SettingChoice.NWS.getValue());
	}
	
	public SettingProfile getNoCommentsProfile() {
		return settingList.get(Settings.SettingChoice.NOC.getValue());
	}
	
	public SettingProfile getNoCWSProfile() {
		return settingList.get(Settings.SettingChoice.NCW.getValue());
	}
	
	public SettingProfile getCommentsProfile() {
		return settingList.get(Settings.SettingChoice.COM.getValue());
	}
	
	public SettingProfile getTokenisedProfile() {
		return settingList.get(Settings.SettingChoice.TOK.getValue());
	}
	
	public SettingProfile getWSPatternProfile() {
		return settingList.get(Settings.SettingChoice.WSP.getValue());
	}
	
	public void initialiseDefault() {
		/* If settings have not been initialised */
		if ( !settingIntitialised ) { 
			settingIntitialised = true ;
			System.out.println("Initialising default");
			System.out.println(getSourceDirectory());
	
			for (int setting = 0; setting < SettingChoice.getNumberOfFileTypes(); setting++) {
				System.out.println(Settings.SettingChoice.values()[setting].toString());
				SettingProfile sp = new SettingProfile( setting, getSourceDirectory(), false);
				settingList.add(sp);
			}
		} else {
			settingIntitialised = false ;
			resetSettingList() ;
			initialiseDefault() ;
		}
	}
	
	public void loadSettings() {
		System.out.println("Loading previous settings");
		System.out.println(getSourceDirectory());
		
		for (int setting = 0; setting < SettingChoice.getNumberOfFileTypes(); setting++) {
			System.out.println(Settings.SettingChoice.values()[setting].toString());
			SettingProfile sp = new SettingProfile( setting, getSourceDirectory(), true );
			settingList.add(sp);
		}
	}

	/**
	 * 
	 */
	public List<Boolean> getInUseStatus() {
		List<Boolean> statuses = new LinkedList<Boolean>();
		for ( SettingProfile sp : getSettingList() ) {
			statuses.add(sp.isInUse());
		}
		System.out.println("Length of list " + statuses.size());
		
		return statuses;
	}
	
}
