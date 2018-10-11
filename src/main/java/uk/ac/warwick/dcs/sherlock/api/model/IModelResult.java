package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;

import java.util.List;

/**
 *
 */
public interface IModelResult {

	/**
	 * Adds a set of matching blocks of code to the results. TODO: What about more than 1 file, should this method take start/end lines for any number of files????
	 *
	 * @param block1 first matched block
	 * @param block2 second matched block
	 */
	void addMatchedBlocks(IContentBlock block1, IContentBlock block2); // OR: void addMatchedBlocks(List<IContentBlock> blocks);

	/**
	 * @return Detector class used to get result set
	 */
	Class<? extends IDetector> getDetector();

	/**
	 * @return set of included files, used for processing algorithm output
	 */
	List<ISourceFile> getIncludedFiles();

    /*
    TODO: extend this api to extract relevant information such as detected blocks, statistics (processing time??).

    Can then be used to run analysis on the results and rank the plagiarised sections in the core program

     List<IMatchedBlocks> getMatchedBlocks();
     */

}
