package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

/**
 * Top level interface workers are required to implement
 */
public abstract class AbstractDetectorWorker<K extends AbstractModelTaskRawResult> {

	/**
	 * Do work and create the results
	 */
	public abstract void execute();

	/**
	 * Gets the results of the worker execution, only minimal processing should be performed in this method
	 *
	 * @return worker results
	 */
	public abstract K getRawResult();

}
