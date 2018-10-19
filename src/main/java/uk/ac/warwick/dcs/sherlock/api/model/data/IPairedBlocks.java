package uk.ac.warwick.dcs.sherlock.api.model.data;

import uk.ac.warwick.dcs.sherlock.api.logging.Logger;

public interface IPairedBlocks {

	/**
	 * @return value is within the range 0 to 1 inclusive
	 */
	static boolean checkPercentageInRange(float value) {
		return (value >= 0) && (value <= 1);
	}

	/**
	 * Construct a default instance of IPairedBlocks, based on the internal api implementation
	 *
	 * @param block1                IContentBlock 1
	 * @param block2                IContentBlock 2
	 * @param percentageMatchBlock1 The percentage of block 1 which contains elements from block 2, float between 0 and 1
	 * @param percentageMatchBlock2 The percentage of block 2 which contains elements from block 1, float between 0 and 1
	 *
	 * @return constructed generic instance
	 */
	static IPairedBlocks of(IContentBlock block1, IContentBlock block2, float percentageMatchBlock1, float percentageMatchBlock2) {
		return new GenericPairedBlocks(block1, block2, percentageMatchBlock1, percentageMatchBlock2);
	}

	IContentBlock getBlock1();

	IContentBlock getBlock2();

	/**
	 * @return The percentage of block 1 which contains elements from block 2, float between 0 and 1
	 */
	float getPercentageMatchBlock1();

	/**
	 * @return The percentage of block 2 which contains elements from block 1, float between 0 and 1
	 */
	float getPercentageMatchBlock2();

	/**
	 * Verifies and then sets the percentage match for both directions
	 *
	 * @param percentageMatchBlock1 The percentage of block 1 which contains elements from block 2, float between 0 and 1
	 * @param percentageMatchBlock2 The percentage of block 2 which contains elements from block 1, float between 0 and 1
	 */
	void setPercentageMatch(float percentageMatchBlock1, float percentageMatchBlock2);

	class GenericPairedBlocks implements IPairedBlocks {

		private IContentBlock block1, block2;
		private float percentageB1, percentageB2;

		private GenericPairedBlocks(IContentBlock block1, IContentBlock block2, float percentageMatchBlock1, float percentageMatchBlock2) {
			this.block1 = block1;
			this.block2 = block2;
			this.setPercentageMatch(percentageMatchBlock1, percentageMatchBlock2);
		}

		@Override
		public void setPercentageMatch(float percentageMatchBlock1, float percentageMatchBlock2) {
			if (IPairedBlocks.checkPercentageInRange(percentageMatchBlock1)) {
				this.percentageB1 = percentageMatchBlock1;
			}
			else {
				this.percentageB1 = 0;
				Logger.log(String.format("percentageMatchBlock1 out of range [%.2f], must be between 0 and 1", percentageMatchBlock1));
			}

			if (IPairedBlocks.checkPercentageInRange(percentageMatchBlock2)) {
				this.percentageB2 = percentageMatchBlock2;
			}
			else {
				this.percentageB2 = 0;
				Logger.log(String.format("percentageMatchBlock2 out of range [%.2f], must be between 0 and 1", percentageMatchBlock2));
			}
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
		public float getPercentageMatchBlock1() {
			return this.percentageB1;
		}

		@Override
		public float getPercentageMatchBlock2() {
			return this.percentageB2;
		}

		@Override
		public String toString() {
			return String.format("%s <--> %s [%.0f%% <--> %.0f%%]", this.block1.toString(), this.block2.toString(), this.percentageB1 * 100, this.percentageB2 * 100);
		}
	}
}
