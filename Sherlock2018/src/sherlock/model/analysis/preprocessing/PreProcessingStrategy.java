/**
 * 
 */
package sherlock.model.analysis.preprocessing;

import java.io.File;

/**
 * @author Aliyah
 *
 */
interface PreProcessingStrategy {

	void preProcessFiles(File[] filePaths, File targetDirectory);

}
