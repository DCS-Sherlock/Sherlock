package uk.ac.warwick.dcs.sherlock.api.event;

public class EventBus {

	private static IEventBus bus;

	public static void mapEventBus(IEventBus bus) {
		EventBus.bus = bus;
	}

	public void publishEvent(IEvent event) {
		bus.publishEvent(event);
	}

	public void registerSubscriber(Class<?> subscriber) {
		bus.registerSubscriber(subscriber);
	}

}
