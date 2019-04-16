package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

import java.util.*;

/**
 * An extension of the basic worker for standard pairwise matching, implements the basic internal data structures
 */
public abstract class PairwiseDetectorWorker<K extends AbstractModelTaskRawResult> extends DetectorWorker<K> {

	protected ModelDataItem file1;
	protected ModelDataItem file2;
	protected K result;

	public PairwiseDetectorWorker() {
		super(null);
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

	/**
	 * Loads data into the worker, called by the {@link PairwiseDetector#buildWorkers(List)} method
	 *
	 * @param file1Data preprocessed data for file 1
	 * @param file2Data preprocessed data for file 2
	 *
	 * @return this (the current worker instance)
	 */
	PairwiseDetectorWorker putData(IDetector parent, ModelDataItem file1Data, ModelDataItem file2Data) {
		this.parent = parent;
		this.file1 = file1Data;
		this.file2 = file2Data;

		return this;
	}
}
