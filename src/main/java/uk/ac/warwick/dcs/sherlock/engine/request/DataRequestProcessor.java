package uk.ac.warwick.dcs.sherlock.engine.request;

import uk.ac.warwick.dcs.sherlock.api.annotations.RequestProcessor;
import uk.ac.warwick.dcs.sherlock.api.request.IRequestReference;
import uk.ac.warwick.dcs.sherlock.api.request.RequestInvocation;

@RequestProcessor(apiFieldName = "dataRequestProcessor")
public class DataRequestProcessor {

	@RequestProcessor.Instance
	public static DataRequestProcessor instance;

	public DataRequestProcessor() {

	}

	@RequestProcessor.PostHandler
	public void handlePost(IRequestReference reference, RequestInvocation source, Object payload) {
		System.out.println("hi: " + payload.toString());

		if (source != null) {
			source.respond(reference, "successful");
		}
	}

}
