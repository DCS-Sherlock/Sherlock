/**
 *
 */
package uk.ac.warwick.dcs.sherlock.deprecated.services.detection;

import uk.ac.warwick.dcs.sherlock.deprecated.SettingProfile;

import java.io.File;
import java.util.*;

/**
 * @author Aliyah
 */
interface DetectionStrategy {

	/**
	 * Method that performs the selected detection algorithm
	 *
	 * @return
	 */
	ArrayList doDetection(File[] filesToCompare, SettingProfile sp, int ngramLength);
}
