/**
 * 
 */
package sherlock.model.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * @author Aliyah
 *
 */
public class SettingProfile {
	
	/**
	 * The properties for this setting profile instance
	 */
	private Properties thisProfile ;
	
	/**
	 * The file that this profile is saved in.
	 */
	private File propertiesFile;
	
	/**
	 * Constructor Method for Setting Profile
	 */
	public SettingProfile(int setting, File sourceDirectory, boolean loadSettings){
		System.out.println("Adding a new profile for setting: " + setting);
		thisProfile = new Properties(createDefaults(setting));
		
		File settingsDirectory = new File(sourceDirectory.getAbsolutePath() + File.separator + "Settings");
		
		setPropertiesFile( new File (settingsDirectory, thisProfile.getProperty("outputDir") + ".txt")) ;
		
		if ( ! loadSettings ) {
			System.out.println("Storing defaults");
			System.out.println("This profile: " + thisProfile.getProperty("description"));
			System.out.println("This profile: " + thisProfile.getProperty("outputDir"));
			System.out.println("This profile: " + thisProfile.getProperty("tokeniserName"));
			System.out.println("This profile: " + thisProfile.getProperty("inUse"));
			store();
		} else if ( loadSettings ) {
			System.out.println("Loading Settings");
			load();
		}
	}

	private Properties getThisProfile() {
		return thisProfile;
	}

	private void setThisProfile(Properties thisProfile) {
		this.thisProfile = thisProfile;
	}

	private File getPropertiesFile() {
		return propertiesFile;
	}

	private void setPropertiesFile(File propertiesFile) {
		this.propertiesFile = propertiesFile;
		System.out.println("Property path " + getPropertiesFile().getAbsolutePath());
	}

	private static String[][] getDefaultproperties() {
		return defaultProperties;
	}

	/**
	 * The default profile settings for each possible setting, i.e. Original, No Whitespace etc. Each array item is itself an
	 * array, with the ordering as follows:
	 *	
	 *	String description
	 *	String Output directory
	 *	String Tokeniser Name
	 *	boolean inUse
	 *
	 *
	 *
	 *
   *   int minStringLength
   *   int minRunLength
   *   int maxForwardJump
   *   int maxBackwardJump
   *   int maxJumpDiff
   *   boolean amalgamate
   *   boolean concatanate
   *   int strictness
	 */
	final static String defaultProperties[][] = {
		/* Original */
		{"Original", "Original", "", "true"},
		/* No WhiteSpace */
		{"No Whitespace", "NoWhitespace", "", "false"},
		/* No Comments */
		{"No Comments", "NoComments", "", "false"},
		/* No Whitespace & no Comments */
		{"No Comments & No Whitespace", "NoWS_NoComment", "", "true"},
		/* Comments */
		{"Comments only", "Comments", "", "false"},
		/* Tokenised */
		{"Tokenised", "Tokenised", "", "true"},
		/* Whitespace Pattern */
		{"Whitespace Pattern", "WSPattern", "", "false"}	
	} ;
	
	/**
	 * Initialises the default settings for each setting profile.
	 * @param setting		- Index to the default properties array defined above
	 * @return				- The Property definition for this setting profile
	 */
	private Properties createDefaults(int setting) {
		/* A string array containing the defaults for this particular setting*/
		String settingDefault[] = defaultProperties[setting];
		System.out.println("Setting Default Description " + settingDefault[0]);
		Properties p = new Properties();
	    p.setProperty("description", settingDefault[0]);
	    p.setProperty("outputDir", settingDefault[1]);
	    p.setProperty("tokeniserName", settingDefault[2]);
	    p.setProperty("inUse", settingDefault[3]);
		return p;
	}
	
	void store() {
	    try {
	      FileOutputStream fos = new FileOutputStream(getPropertiesFile());
	      /* The default properties don't need to be saved each time because they never change */
	      /* The store method needs a stream to write to, as well as a string that it uses as a comment at the top of the output. */
	      thisProfile.store(fos, getDescription());
	      fos.close();
	    }
	    catch (java.io.IOException e) {
		   e.printStackTrace();
	    }
	  }
	
	/**
	 * Set this setting profile to a setting profile loaded from file .
	 */
	public void load() {
		System.out.println("The loaded property file: " + getPropertiesFile());
		try {
			FileInputStream fis = new FileInputStream(getPropertiesFile());
			thisProfile.load(fis);
//			System.out.println("This profile: " + thisProfile.getProperty("description"));
//			System.out.println("This profile: " + thisProfile.getProperty("outputDir"));
//			System.out.println("This profile: " + thisProfile.getProperty("tokeniserName"));
//			System.out.println("This profile: " + thisProfile.getProperty("inUse"));
			fis.close();
	    }
		catch (java.io.IOException e) {
			e.printStackTrace();
	    }
	}
	
	/**
	 * Returns the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return thisProfile.getProperty("description");
	}
	
	/**
	 * Set the description of this setting profile
	 * @param s 		- The new description
	 */
	public void setDescription(String s) {
		thisProfile.setProperty("description", s);
	}

	/**
 	 * Returns the output directory.
	 *
	 * @return the output directory
	 */
	public String getDirectory() {
		return thisProfile.getProperty("outputDir");
	}
	
	/**
	 * Set the output directory of this setting profile
	 * @param s 		- The new output directory
	 */
	public void setOutputDir(String s) {
		thisProfile.setProperty("outputDir", s);
	}

	/**
	 * Returns the tokeniser name.
	 *
	 * @return the tokeniser name
	 */
	public String getTokeniserName() {
		return thisProfile.getProperty("tokeniserName");
	}

	/**
	 * Set the tokeniser name of this setting profile
	 * @param s 		- The new tokeniser name
	 */
	public void setTokeniserName(String s) {
		thisProfile.setProperty("tokeniserName", s);
	}
	
	/**
	 * Returns whether this file type is in use.
	 *
	 * @return 		- True it is in use, false otherwise
	 */
	public boolean isInUse() {
		return Boolean.valueOf(thisProfile.getProperty("inUse")).booleanValue();
	}
	
	/**
	 * Set the in use parameter of this setting profile
	 * @param s 		- The new in use status
	 */
	public void setInUser(Boolean b) {
		thisProfile.setProperty("inUse", String.valueOf(b));
	}
	

}
