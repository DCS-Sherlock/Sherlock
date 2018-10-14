package uk.ac.warwick.dcs.sherlock.api.model.data;

import java.util.List;

/**
 *
 */
public interface IModelResultItem {

	/**
	 * Adds a set of matching blocks of code to the results.
	 *
	 * @param block1          first matched block
	 * @param block2          second matched block
	 * @param percentageMatch the percentage the two block match, float between 0 and 1
	 */
	default void addPairedBlocks(IContentBlock block1, IContentBlock block2, float percentageMatch) {
		this.addPairedBlocks(IPairedBlocks.of(block1, block2, percentageMatch));
	}

	/**
	 * Adds a set of matching blocks of code to the results.
	 *
	 * @param blockPair Pair of matching code blocks
	 */
	void addPairedBlocks(IPairedBlocks blockPair);

	/*
	TODO: extend this api to extract relevant information such as detected blocks, statistics (processing time??).

	Can then be used to run analysis on the results and rank the plagiarised sections in the base program
	*/
	List<IPairedBlocks> getAllPairedBlocks();

}
