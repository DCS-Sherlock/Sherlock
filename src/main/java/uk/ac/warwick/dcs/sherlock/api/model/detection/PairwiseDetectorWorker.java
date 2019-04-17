package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

/**
 * An extension of the basic worker for standard pairwise matching, implements the basic internal data structures
 */
public abstract class PairwiseDetectorWorker<K extends AbstractModelTaskRawResult> extends DetectorWorker<K> {

	protected ModelDataItem file1;
	protected ModelDataItem file2;
	protected K result;

	/**
	 * Loads data into the worker
	 *
	 * @param parent the owning detector
	 * @param file1Data preprocessed data for file 1
	 * @param file2Data preprocessed data for file 2
	 */
	public PairwiseDetectorWorker(IDetector parent, ModelDataItem file1Data, ModelDataItem file2Data) {
		super(parent);

		this.file1 = file1Data;
		this.file2 = file2Data;
	}

	/**
	 * Gets the results of the worker execution, only minimal processing should be performed in this method
	 *
	 * @return worker results
	 */
	@Override
	public K getRawResult() {
		return this.result;
	}
}
