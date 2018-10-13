package uk.ac.warwick.dcs.sherlock.deprecated.services.fileSystem;

import java.io.File;

/**
 * @author Aliyah
 *
 */
class ExtractionContext implements ExtractionStrategy {

	private ExtractionStrategy ex ;
	
	/**
	 * The DetectionContext Constructor
	 * @param ex				- The extraction strategy to be used
	 * @param dir			- The source directory
	 * @param destination	- The destination the files are to be extracted to
	 */
	ExtractionContext(ExtractionStrategy ex, File[] dir, String destination){
		this.ex = ex;
		extract(dir, destination);
	}
	
	/**
	 * Calls the correct decompression method.
	 * @param dir 			- The input directory to be extracted
	 * @param destination 	- The destination the files are to be extracted to   	
	 */
	@Override
	public void extract(File[] dir, String destination) {
		ex.extract(dir, destination) ;
	}

	/**
	 * Returns the Extraction strategy
	 * @return		- The extraction strategy being used
	 */
	ExtractionStrategy getEx() {
		return ex;
	}

	/**
	 * Method to set the Extraction strategy. This method will also call the extraction tokenise to update the files
	 * @param ex				- The new extraction strategy to be used.
	 * @param dir			- The source directory
	 * @param destination	- The destination the files are to be extracted to
	 */
	void setEx(ExtractionStrategy ex, File[] dir, String destination) {
		this.ex = ex;
		extract(dir, destination);
	}
	
}