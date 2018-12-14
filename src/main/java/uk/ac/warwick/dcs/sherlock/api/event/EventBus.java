package uk.ac.warwick.dcs.sherlock.api.event;

/**
 * Static wrapper for the event bus (IEventBus implementation). Allows any class to subscribe and publish events to the bus
 */
public class EventBus {

	private static IEventBus bus;

	/**
	 * Publish an event to the bus
	 *
	 * @param event to publish
	 */
	public static void publishEvent(IEvent event) {
		bus.publishEvent(event);
	}

	/**
	 * Attempts to register an object as an event subscriber, all methods with @EventHandler annotation will be registered
	 *
	 * @param subscriber instance of a class to register
	 */
	public static void registerEventSubscriber(Object subscriber) {
		bus.registerEventSubscriber(subscriber);
	}

}
