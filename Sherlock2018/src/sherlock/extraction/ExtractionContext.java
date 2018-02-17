package sherlock.extraction;

import java.io.File;

/**
 * @author Aliyah
 *
 */
class ExtractionContext implements ExtractionStrategy {

	private ExtractionStrategy ex ;
	
	/**
	 * The DetectionContext Constructor
	 * @param ex			- The extraction strategy to be used
	 * @param dir		- The source directory
	 */
	ExtractionContext(ExtractionStrategy ex, File[] dir){
		this.ex = ex;
		extract(dir);
	}
	
	/**
	 * Calls the correct decompression method.
	 * @param dir 	- The input directory to be extracted
	 * @return 		- A collection of files that are to be passed on to the parsing phase
	 */
	@Override
	public void extract(File[] dir) {
		ex.extract(dir) ;
	}

	/**
	 * Returns the Extraction strategy
	 * @return		- The extraction strategy being used
	 */
	ExtractionStrategy getEx() {
		return ex;
	}

	/**
	 * Method to set the Extraction strategy. This method will also call the extraction process to update the files
	 * @param ex			- The new extraction strategy to be used.
	 * @param dir		- The source directory
	 */
	void setEx(ExtractionStrategy ex, File[] dir) {
		this.ex = ex;
		extract(dir);
	}
	
}