package uk.ac.warwick.dcs.sherlock.api.common;

import java.util.*;

public class RequestDatabase {

	private static Class<?> registry = null;

	public enum RegistryRequests implements IRequestReference {
		GET_DETECTORS(Map.class),
		GET_DETECTORS_NAMES(LinkedList.class);

		private final Class<?> returnType;

		RegistryRequests(Class<?> returnType) {
			this.returnType = returnType;
		}

		@Override
		public Class<?> getHandler() {
			return registry;
		}

		@Override
		public Class<?> getReturnType() {
			return this.returnType;
		}
	}

}
