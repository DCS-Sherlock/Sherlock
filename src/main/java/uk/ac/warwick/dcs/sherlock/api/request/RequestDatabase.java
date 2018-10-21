package uk.ac.warwick.dcs.sherlock.api.request;

public class RequestDatabase {

	private static Class<?> dataRequestProcessor = null;

	public enum DataRequestReference implements IRequestReference {
		REQ1;

		@Override
		public Class getHandler() {
			return dataRequestProcessor;
		}
	}

}
