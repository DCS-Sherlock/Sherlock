package uk.ac.warwick.dcs.sherlock.api.event;

/**
 * Static wrapper for the event bus (IEventBus implementation). Allows any class to subscribe and publish events to the bus
 */
public class EventBus {

	private static IEventBus bus;

	public static void publishEvent(IEvent event) {
		bus.publishEvent(event);
	}

	public static void registerEventSubscriber(Object subscriber) {
		bus.registerEventSubscriber(subscriber);
	}

}
