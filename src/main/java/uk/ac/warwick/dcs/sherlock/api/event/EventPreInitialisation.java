package uk.ac.warwick.dcs.sherlock.api.event;

public class EventPreInitialisation implements IEventModule {

	private String[] launchArgs;

	public EventPreInitialisation(String[] launchArgs) {
		this.launchArgs = launchArgs;
	}

	public String[] getLaunchArgs() {
		return this.launchArgs;
	}

}
