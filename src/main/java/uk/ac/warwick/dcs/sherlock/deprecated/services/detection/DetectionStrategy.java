/**
 * 
 */
package uk.ac.warwick.dcs.sherlock.deprecated.services.detection;

import java.io.File;
import java.util.ArrayList;

import uk.ac.warwick.dcs.sherlock.deprecated.SettingProfile;

/**
 * @author Aliyah
 *
 */
interface DetectionStrategy {
	/**
	 * Method that performs the selected detection algorithm 
	 * @return 
	 */
	ArrayList doDetection(File[] filesToCompare, SettingProfile sp, int ngramLength);
}
