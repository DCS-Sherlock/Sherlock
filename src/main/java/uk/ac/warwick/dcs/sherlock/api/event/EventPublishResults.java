package uk.ac.warwick.dcs.sherlock.api.event;

/**
 * Not Used!!! Maybe Future work
 */
public class EventPublishResults implements IEvent {

	private String results;

	public EventPublishResults(String results) {
		this.results = results;
	}

	public String getResults() {
		return this.results;
	}

}
