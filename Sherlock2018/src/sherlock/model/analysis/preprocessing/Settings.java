package sherlock.model.analysis.preprocessing;

import java.io.File;
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
		
		int getValue() {
	        return value;
	    }
		
		static int getNumberOfFileTypes() {
			return values().length ;
		}
		
	}
	
//	Settings.SettingChoice original = Settings.SettingChoice.ORI;
//	Settings.SettingChoice noWhiteSpace = Settings.SettingChoice.NWS; 
//	Settings.SettingChoice noComments = Settings.SettingChoice.NOC; 
//	Settings.SettingChoice noWS_NoComments = Settings.SettingChoice.NCW; 
//	Settings.SettingChoice comments = Settings.SettingChoice.COM; 
//	Settings.SettingChoice tokenised = Settings.SettingChoice.TOK;
//	Settings.SettingChoice whitespacePattern = Settings.SettingChoice.WSP;
	
	/**
	 * The directory that holds the source code to be compared.
	 */
	private File sourceDirectory;
	
	/**
	 * A list of settings selected by the user
	 *  
	 *  Example:
	 *  
	 *  [(profile0), (profile1)... (profile2) ]
	 *  where numbers relate to file_type enums 
	 * 	File types that aren't being used are set to false in the profile
	 */
	private List<SettingProfile> settingList = null;
	
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
	
	public void initialiseDefault() {
		System.out.println("Initialising default");
		System.out.println(getSourceDirectory());

		for (int setting = 0; setting < SettingChoice.getNumberOfFileTypes(); setting++) {
			System.out.println(Settings.SettingChoice.values()[setting].toString());
			new SettingProfile( setting, getSourceDirectory(), false );
		}
	}
	
	public void loadSettings() {
		System.out.println("Loading previous settings");
		System.out.println(getSourceDirectory());
		
		for (int setting = 0; setting < SettingChoice.getNumberOfFileTypes(); setting++) {
			System.out.println(Settings.SettingChoice.values()[setting].toString());
			new SettingProfile( setting, getSourceDirectory(), true );
		}
	}
	
}
