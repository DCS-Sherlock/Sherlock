package uk.ac.warwick.dcs.sherlock.api.common;

public interface IRequestBus {

	/**
	 * Blocking request which returns the result
	 *
	 * @param reference request identifier
	 *
	 * @return request containing result
	 */
	Request post(Request reference);

	/**
	 * Non-blocking request which returns the result to a @ResponseHandler method in the source if one is present
	 *
	 * @param reference request identifier
	 * @param source    object sending the request, to return the result to
	 *
	 * @return whether the request was successfully published
	 */
	boolean post(Request reference, Object source);

}
