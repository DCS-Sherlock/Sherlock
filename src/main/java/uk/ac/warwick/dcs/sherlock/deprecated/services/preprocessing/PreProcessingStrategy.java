/**
 * 
 */
package uk.ac.warwick.dcs.sherlock.deprecated.services.preprocessing;

import java.io.File;

/**
 * @author Aliyah
 *
 */
interface PreProcessingStrategy {

	void preProcessFiles(File[] filePaths, File targetDirectory);

}
