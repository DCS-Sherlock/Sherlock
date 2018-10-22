package uk.ac.warwick.dcs.sherlock.api.common;

public interface IRequestBus {

	/**
	 * Blocking request which returns the result
	 *
	 * @param reference request identifier
	 * @param payload   data to accompany the request
	 *
	 * @return result of the request
	 */
	Object post(IRequestReference reference, Object payload);

	/**
	 * Non-blocking request which returns the result to a @ResponseHandler method in the source if one is present
	 *
	 * @param reference request identifier
	 * @param source    object sending the request, to return the result to
	 * @param payload   data to accompany the request
	 *
	 * @return whether the request was successfully published
	 */
	boolean post(IRequestReference reference, Object source, Object payload);

}
