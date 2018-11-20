package uk.ac.warwick.dcs.sherlock.api.request;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector.TuneableParameter;

import java.util.*;

public class RequestDatabase {

	private static Class<?> registry = null;
	private static Class<?> taskManager = null;

	public abstract static class RegistryRequests<P, R> extends AbstractRequest<P, R> {

		@Override
		public Class<?> getHandler() {
			return registry;
		}

		public static class GetDetectors extends RegistryRequests<Object, Map<String, Class<? extends IDetector>>> {}

		public static class GetDetectorNames extends RegistryRequests<Object, List<String>> {}

		public static class GetTuneableParameters extends RegistryRequests<Class<? extends IDetector>, List<TuneableParameter>> {}
	}

	public abstract static class TaskRequests<P, R> extends AbstractRequest<P, R> {

		@Override
		public Class<?> getHandler() {
			return taskManager;
		}

		/**
		 * Payload is the string name of the detector to use
		 * <p><p>
		 * Response is the id of the task for monitoring
		 */
		public static class RunTask extends TaskRequests<String, Integer> {

			//set other required params here, list of files?
			private Map<String, Float> parameters;

			public Map<String, Float> getParameters() {
				return this.parameters;
			}

			public RunTask setParameters(Map<String, Float> params) {
				this.parameters = params;
				return this;
			}

		}
	}

}
