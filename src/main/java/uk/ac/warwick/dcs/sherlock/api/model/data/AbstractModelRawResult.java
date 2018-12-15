package uk.ac.warwick.dcs.sherlock.api.model.data;

import java.io.Serializable;

public abstract class AbstractModelRawResult implements Serializable {

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
	public abstract boolean testType(AbstractModelRawResult baseline);

	public abstract boolean isEmpty();

}
