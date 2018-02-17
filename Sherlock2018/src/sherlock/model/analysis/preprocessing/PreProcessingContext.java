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
	PreProcessingContext(PreProcessingStrategy ps) {
		this.ps = ps ;
	}
	
	/**
	 * Calls the correct detection method.
	 */
	void executePreProcessing() {			// Add type to collection<>
		ps.preProcessFiles();
	}
}
