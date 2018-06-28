/**
 * 
 */
package uk.ac.warwick.dcs.sherlock.services.preprocessing;

import java.io.File;

/**
 * @author Aliyah
 *
 */
interface PreProcessingStrategy {

	void preProcessFiles(File[] filePaths, File targetDirectory);

}
