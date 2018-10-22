package uk.ac.warwick.dcs.sherlock.api.request;

public class RequestBus {

	private static IRequestBus bus;

	/**
	 * Blocking request which returns the result
	 *
	 * @param reference request identifier
	 *
	 * @return result of the request
	 */
	public static <R extends AbstractRequest> R post(R reference) {
		return (R) bus.post(reference);
	}

	/**
	 * Non-blocking request which returns the result to a @ResponseHandler method in the source if one is present
	 *
	 * @param reference request identifier
	 * @param source    object sending the request, to return the result to
	 *
	 * @return whether the request was successfully published
	 */
	public static boolean post(AbstractRequest reference, Object source) {
		return bus.post(reference, source);
	}

}
