package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

import java.util.*;

/**
 * An extension of the basic worker for standard pairwise matching, implements the basic internal data structures
 */
public abstract class AbstractPairwiseDetectorWorker<K extends AbstractModelTaskRawResult> extends IDetectorWorker<K> {

	protected ModelDataItem file1;
	protected ModelDataItem file2;
	protected K result;

	/**
	 * Gets the results of the worker execution, only minimal processing should be performed in this method
	 *
	 * @return worker results
	 */
	@Override
	public K getRawResult() {
		return this.result;
	}

	/**
	 * Loads data into the worker, called by the {@link AbstractPairwiseDetector#buildWorkers(List)} method
	 *
	 * @param file1Data preprocessed data for file 1
	 * @param file2Data preprocessed data for file 2
	 *
	 * @return this (the current worker instance)
	 */
	AbstractPairwiseDetectorWorker putData(ModelDataItem file1Data, ModelDataItem file2Data) {
		this.file1 = file1Data;
		this.file2 = file2Data;

		return this;
	}
}
