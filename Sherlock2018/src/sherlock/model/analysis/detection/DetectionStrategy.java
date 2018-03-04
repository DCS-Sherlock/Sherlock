/**
 * 
 */
package sherlock.model.analysis.detection;

import java.io.File;

import sherlock.model.analysis.SettingProfile;

/**
 * @author Aliyah
 *
 */
interface DetectionStrategy {
	/**
	 * Method that performs the selected detection algorithm 
	 */
	void doDetection(File[] filesToCompare, SettingProfile sp);
}
