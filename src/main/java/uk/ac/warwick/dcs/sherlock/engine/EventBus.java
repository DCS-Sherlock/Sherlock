package uk.ac.warwick.dcs.sherlock.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotations.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.annotations.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.event.IEvent;
import uk.ac.warwick.dcs.sherlock.api.event.IEventBus;
import uk.ac.warwick.dcs.sherlock.api.event.IEventModule;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class EventBus implements IEventBus {

	final Logger logger = LoggerFactory.getLogger(EventBus.class);
	private Map<Class<? extends IEvent>, List<EventInvocation>> eventMap;

	EventBus() {
		this.eventMap = new HashMap<>();
	}

	private void addInvocation(Class<? extends IEvent> event, EventInvocation invocation) {
		if (!this.eventMap.containsKey(event)) {
			this.eventMap.put(event, new LinkedList<>());
		}
		this.eventMap.get(event).add(invocation);
	}

	@Override
	public void publishEvent(IEvent event) {
		this.eventMap.get(event.getClass()).parallelStream().forEach(x -> x.invoke(event));
	}

	@Override
	public void registerEventSubscriber(Object subscriber) {
		Arrays.stream(subscriber.getClass().getMethods()).filter(x -> x.isAnnotationPresent(EventHandler.class)).forEach(x -> {
			if (x.getParameterTypes().length != 1) {
				logger.warn("Wrong number of params", x);
			}
			else if (x.getParameterTypes()[0].getSuperclass().isAssignableFrom(IEventModule.class)) {
				logger.warn("Subscribers may not handle SherlockModule events", x);
			}
			else {
				logger.info("registered event handler:" + subscriber.getClass().getName());
				this.addInvocation(x.getParameterTypes()[0].asSubclass(IEvent.class), EventInvocation.of(x, subscriber));
			}
		});
	}

	void registerModule(Class<?> module) {
		if (!module.getAnnotation(SherlockModule.class).side().valid(SherlockEngine.side)) {
			return;
		}

		try {
			Object obj = module.newInstance();

			Arrays.stream(module.getMethods()).filter(x -> x.isAnnotationPresent(EventHandler.class)).forEach(x -> {
				if (x.getParameterTypes().length != 1) {
					System.out.println("Wrong number of params");
				}
				else {
					this.addInvocation(x.getParameterTypes()[0].asSubclass(IEvent.class), EventInvocation.of(x, obj));
				}

			});
		}
		catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	void removeInvocationsOfEvent(Class<? extends IEvent> event) {
		this.eventMap.remove(event);
	}

	static class EventInvocation extends Tuple<Method, Object> {

		EventInvocation(Method method, Object obj) {
			super(method, obj);
		}

		void invoke(IEvent event) {
			try {
				this.getKey().invoke(this.getValue(), event);
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		static EventInvocation of(Method method, Object obj) {
			return new EventInvocation(method, obj);
		}
	}

}
