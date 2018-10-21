package uk.ac.warwick.dcs.sherlock.api.event;

public class EventBus {

	private static IEventBus bus;

	public static void publishEvent(IEvent event) {
		bus.publishEvent(event);
	}

	public static void registerEventSubscriber(Object subscriber) {
		bus.registerEventSubscriber(subscriber);
	}

}
