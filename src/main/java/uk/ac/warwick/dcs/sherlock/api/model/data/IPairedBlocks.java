package uk.ac.warwick.dcs.sherlock.api.model.data;

import uk.ac.warwick.dcs.sherlock.api.model.data.internal.PairedBlocks;

public interface IPairedBlocks {

	static boolean checkPercentageInRange(float value) {
		return (value >= 0) && (value <= 1);
	}

	static IPairedBlocks of(IContentBlock block1, IContentBlock block2, float percentageMatch) {
		return new PairedBlocks(block1, block2, percentageMatch);
	}

	IContentBlock getBlock1();

	IContentBlock getBlock2();

	/**
	 * @return The percentage the two blocks match, float between 0 and 1
	 */
	float getPercentageMatch();

	void setPercentageMatch(float percentage);

}
