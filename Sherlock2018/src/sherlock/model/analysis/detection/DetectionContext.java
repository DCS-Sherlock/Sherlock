/**
 * 
 */
package sherlock.model.analysis.detection;

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
	 * @param workingDirectory 	- a collection of the tokenised files to perform the detection over
	 */
	void executeDetection() {
		ds.doDetection();
	}
}
