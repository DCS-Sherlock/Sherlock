package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.common.event.IEvent;

public interface IEventBus {

	void publishEvent(IEvent event);

	void registerEventSubscriber(Object subscriber);

}
