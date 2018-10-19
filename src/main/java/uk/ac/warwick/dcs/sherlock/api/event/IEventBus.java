package uk.ac.warwick.dcs.sherlock.api.event;

public interface IEventBus {

	void publishEvent(IEvent event);

	void registerEventSubscriber(Object subscriber);

}
