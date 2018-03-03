/**
 * 
 */
package sherlock.model.analysis.preprocessing;

import java.io.File;

/**
 * @author Aliyah
 *
 */
class PreProcessingContext {
	private PreProcessingStrategy ps;
	
	/**
	 * The PreProcessingContext Constructor
	 * @param ps		- The pre-processing strategy to be executed.
	 */
	PreProcessingContext(PreProcessingStrategy ps, File[] filePaths, File file) {
		this.ps = ps ;
		executePreProcessing(filePaths, file);
	}
	
	/**
	 * Calls the correct detection method and stores the result of the pre-processing to file
	 */
	void executePreProcessing(File[] filePaths, File targetDirectory) {			// Add type to collection<>
		ps.preProcessFiles(filePaths, targetDirectory);
	}
}
