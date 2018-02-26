/**
 * 
 */
package sherlock.model.analysis.preprocessing;

/**
 * @author Aliyah
 *
 */
interface PreProcessingStrategy {

	void preProcessFiles(String[] filePaths, String targetDirectory);

}
