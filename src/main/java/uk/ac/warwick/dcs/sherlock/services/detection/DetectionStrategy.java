/**
 * 
 */
package uk.ac.warwick.dcs.sherlock.services.detection;

import java.io.File;
import java.util.ArrayList;

import uk.ac.warwick.dcs.sherlock.SettingProfile;

/**
 * @author Aliyah
 *
 */
interface DetectionStrategy {
	/**
	 * Method that performs the selected detection algorithm 
	 * @return 
	 */
	ArrayList doDetection(File[] filesToCompare, SettingProfile sp);
}
