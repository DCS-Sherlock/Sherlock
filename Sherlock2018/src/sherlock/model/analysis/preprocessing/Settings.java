package sherlock.model.analysis.preprocessing;

import java.io.File;
import java.util.List;

public class Settings {
	private enum SettingChoice{
		/**
		 * Numeric value for the original.
		 */
		ORIGINAL(0),
		
		/**
		 * Numeric value for no-whitespace.
		 */
		NO_WS(1),
		
		/**
		 * Numeric value for no comment.
		 */
		NOCOMMENT(2),
		
		/**
		 * Numerical value for no comment no white.
		 */
		NOCOM_NOWS(3),
		

		/**
 		* Numeric value for comment.
 		*/
		COMMENT(4),
		
		/**
		 * Numeric value for the tokenised files.
		 */
		TOKENS(5);
		
		private int value;
		
		private SettingChoice(int value) {
			this.value = value;
		}
		
		int getValue() {
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
	
	/**
	 * A list of settings selected by the user
	 *  
	 *  Example:
	 *  
	 *  [(0, Profile), (4, profile)... ]
	 *  where numbers relate to file_type enums 
	 * 	not all file types are included - if not selected by user
	 * 
	 * 	[SettingProfile1, SettingProfile2, SettingProfile3 ]
	 * 	
	 * where profiles contain setting type and other information relating 
	 */
//	private List<> settingList = null;
	
	/**
	 * Setting Constructor
	 */
	public Settings(){}
	
	public File getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
		System.out.println("Source Directory: " + sourceDirectory);
	}

//	public void addSetting(SettingChoice s /*, SettingProfile sp*/) {
//		
//	}
	
}
