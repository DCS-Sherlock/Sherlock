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
class SameLinesStrategy implements DetectionStrategy {

	@Override
	public void doDetection(File[] filesToCompare, SettingProfile sp) {
		System.out.println("Detection Strategy: \t Samelines Detection");
		
	}

}
