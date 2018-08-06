/**
 * 
 */
package uk.ac.warwick.dcs.sherlock.services.detection;

import java.io.File;

import uk.ac.warwick.dcs.sherlock.SettingProfile;

/**
 * This class stores which detection strategy is to be used and provides functionality to execute the algorithm.
 * @author Aliyah
 *
 */
class DetectionContext {
	private DetectionStrategy ds;
	
	/**
	 * The DetectionContext Constructor
	 * @param ds		- The detection strategy to be executed.
	 */
	DetectionContext(DetectionStrategy ds) {
		this.ds = ds;
	}
	
	/**
	 * Calls the correct detection method.
	 * @param filesToCompare 	- a collection of the tokenised files to perform the detection over
	 * @param sp 				- The setting profile defining the pre-processed versions to detect over 
	 */
	void executeDetection(File[] filesToCompare, SettingProfile sp) {
		ds.doDetection(filesToCompare, sp, 30);
	}
}