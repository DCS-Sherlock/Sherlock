package uk.ac.warwick.dcs.sherlock.api.request;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;

import java.util.*;

public class RequestDatabase {

	private static Class<?> registry = null;

	public abstract static class RegistryRequests<P, R> extends AbstractRequest<P,R> {

		public static class GetDetectors extends RegistryRequests<Object, Map<String, Class<? extends IDetector>>> {}
		public static class GetDetectorNames extends RegistryRequests<Object, List<String>> {}

		@Override
		public Class<?> getHandler() {
			return registry;
		}
	}

}
