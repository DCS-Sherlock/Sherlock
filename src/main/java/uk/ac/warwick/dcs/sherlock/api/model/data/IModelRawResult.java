package uk.ac.warwick.dcs.sherlock.api.model.data;

import java.io.Serializable;

public interface IModelRawResult extends Serializable {

	/**
	 * Check that this object is of the same exact type as the baseline, including check any generic types are equal
	 *
	 * TODO: suggested implementation here.....
	 *
	 * @param baseline the baseline object, in the set, current instance must be of the same exact type as this
	 * @return is same type?
	 */
	boolean testType(IModelRawResult baseline);

}
