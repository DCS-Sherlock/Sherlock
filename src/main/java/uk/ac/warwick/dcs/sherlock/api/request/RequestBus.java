package uk.ac.warwick.dcs.sherlock.api.request;

import javax.validation.constraints.NotNull;

public class RequestBus {

	private static IRequestBus bus;

	public static boolean post(@NotNull IRequestReference reference, @NotNull Object source, Object payload) {
		return bus.post(reference, source, payload);
	}

}
