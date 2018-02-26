/**
 * 
 */
package sherlock.model.analysis.preprocessing;

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
	PreProcessingContext(PreProcessingStrategy ps, String[] filePaths, String targetDirectory) {
		this.ps = ps ;
		executePreProcessing(filePaths, targetDirectory);
	}
	
	/**
	 * Calls the correct detection method and stores the result of the pre-processing to file
	 */
	void executePreProcessing(String[] filePaths, String targetDirectory) {			// Add type to collection<>
		ps.preProcessFiles(filePaths, targetDirectory);
	}
}
