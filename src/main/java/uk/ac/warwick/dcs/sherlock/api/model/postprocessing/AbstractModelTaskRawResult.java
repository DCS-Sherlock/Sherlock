package uk.ac.warwick.dcs.sherlock.api.model.postprocessing;

import java.io.Serializable;

public abstract class AbstractModelTaskRawResult implements Serializable {

	private static final long serialVersionUID = 24L;

	/**
	 * Check that this object is of the same exact type as the baseline, including check any generic types are equal
	 * <p>
	 * TODO: suggested implementation here.....
	 *
	 * @param baseline the baseline object, in the set, current instance must be of the same exact type as this
	 *
	 * @return is same type?
	 */
	public abstract boolean testType(AbstractModelTaskRawResult baseline);

	/**
	 * Tests if the rawResult set should be discarded, as it contains no results data
	 * @return is empty of data
	 */
	public abstract boolean isEmpty();

}
