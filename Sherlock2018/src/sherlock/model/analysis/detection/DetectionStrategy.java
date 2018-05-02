/**
 * 
 */
package sherlock.model.analysis.detection;

import java.io.File;
import java.util.ArrayList;

import sherlock.model.analysis.SettingProfile;

/**
 * @author Aliyah
 *
 */
interface DetectionStrategy {
	/**
	 * Method that performs the selected detection algorithm 
	 * @return 
	 */
	void doDetection(File[] filesToCompare, SettingProfile sp);
}
