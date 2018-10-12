package uk.ac.warwick.dcs.sherlock.api.model.internal;

import uk.ac.warwick.dcs.sherlock.api.logging.Logger;
import uk.ac.warwick.dcs.sherlock.api.model.IContentBlock;
import uk.ac.warwick.dcs.sherlock.api.model.IPairedBlocks;

public class PairedBlocks implements IPairedBlocks {

	private IContentBlock block1, block2;
	private float percentage;

	public PairedBlocks(IContentBlock block1, IContentBlock block2, float percentageMatch) {
		this.block1 = block1;
		this.block2 = block2;
		this.setPercentageMatch(percentageMatch);
	}

	@Override
	public IContentBlock getBlock1() {
		return this.block1;
	}

	@Override
	public IContentBlock getBlock2() {
		return this.block2;
	}

	@Override
	public float getPercentageMatch() {
		return this.percentage;
	}

	@Override
	public void setPercentageMatch(float percentage) {
		if (IPairedBlocks.checkPercentageInRange(percentage)) {
			this.percentage = percentage;
		}
		else {
			this.percentage = 0;
			Logger.log("Percentage match out of range, must be between 0 and 1");
		}
	}
}
