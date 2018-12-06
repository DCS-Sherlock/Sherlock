package uk.ac.warwick.dcs.sherlock.api.request;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector.DetectorParameter;

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

		public static class GetTuneableParameters extends RegistryRequests<Class<? extends IDetector>, List<DetectorParameter>> {}
	}

	public abstract static class TaskRequests<P, R> extends AbstractRequest<P, R> {

		@Override
		public Class<?> getHandler() {
			return taskManager;
		}

		/**
		 * Payload is the id of the workspace
		 * <p><p>
		 * Response is the id of the task for monitoring
		 */
		public static class RunDetectorTask extends TaskRequests<Long, Integer> {

			//set other required params here, list of files?
			private String detector;
			private Map<String, Float> parameters;

			public String getDetector() {
				return detector;
			}

			public RunDetectorTask setDetector(String detector) {
				this.detector = detector;
				return this;
			}

			public Map<String, Float> getParameters() {
				return this.parameters;
			}

			public RunDetectorTask setParameters(Map<String, Float> params) {
				this.parameters = params;
				return this;
			}

		}

		// TODO: Consider create another RunTask for one-off detectors, allow cross workspaces, or one-off files not assigned to a workspace/student
	}

}
