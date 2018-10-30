package uk.ac.warwick.dcs.sherlock.api.event;

public interface IEventBus {

	/**
	 * Publish an event to the bus
	 * @param event to publish
	 */
	void publishEvent(IEvent event);

	/**
	 * Attempts to register an object as an event subscriber, all methods with @EventHandler annotation will be registered
	 * @param subscriber instance of a class to register
	 */
	void registerEventSubscriber(Object subscriber);

}
