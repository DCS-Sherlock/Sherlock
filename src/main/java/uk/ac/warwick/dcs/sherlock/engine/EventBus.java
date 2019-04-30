package uk.ac.warwick.dcs.sherlock.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotation.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.annotation.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.event.IEvent;
import uk.ac.warwick.dcs.sherlock.api.event.IEventBus;
import uk.ac.warwick.dcs.sherlock.api.event.IEventModule;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Event bus API implementation, publishes events to listeners
 */
class EventBus implements IEventBus {

	final Logger logger = LoggerFactory.getLogger(EventBus.class);
	private Map<Class<? extends IEvent>, List<EventInvocation>> eventMap;

	EventBus() {
		this.eventMap = new ConcurrentHashMap<>();
	}

	@Override
	public void publishEvent(IEvent event) {
		List<EventInvocation> list = this.eventMap.get(event.getClass());
		if (list != null) {
			list.stream().forEach(x -> x.invoke(event));
		}
	}

	@Override
	public void registerEventSubscriber(Object subscriber) {
		Arrays.stream(subscriber.getClass().getDeclaredMethods()).filter(x -> x.isAnnotationPresent(EventHandler.class)).forEach(x -> {
			if (x.getParameterTypes().length != 1) {
				logger.warn("Event Handlers can only have 1 parameter, {}.{} has {}", subscriber.getClass().getName(), x.getName(), x.getParameterTypes().length);
			}
			else if (!x.getAnnotation(EventHandler.class).side().valid(SherlockEngine.side)) {
				logger.info("{} not registered, running on Side.{}", x.toGenericString().split(" ")[2], SherlockEngine.side.name());
			}
			else if (Arrays.asList(x.getParameterTypes()[0].getInterfaces()).contains(IEventModule.class)) {
				try {
					logger.warn("Could not register {}, subscribers may not handle SherlockModule events", x.toGenericString().split(" ")[2]);
				}
				catch (Exception e) {
					logger.warn("Could not register {}", x.toGenericString().split(" ")[2]);
				}
			}
			else if (Arrays.asList(x.getParameterTypes()[0].getInterfaces()).contains(IEvent.class)) {
				logger.info("Registered event handler: " + subscriber.getClass().getName());
				this.addInvocation(x.getParameterTypes()[0].asSubclass(IEvent.class), EventInvocation.of(x, subscriber));
			}
		});
	}

	void registerModule(Class<?> module) {
		if (!module.getAnnotation(SherlockModule.class).side().valid(SherlockEngine.side)) {
			logger.info("{} not registered, running on Side.{}", module.getName(), SherlockEngine.side.name());
			return;
		}

		try {
			Object obj = module.getConstructor().newInstance();

			List<Field> field = Arrays.stream(module.getDeclaredFields()).filter(x -> x.isAnnotationPresent(SherlockModule.Instance.class)).collect(Collectors.toList());
			if (field.size() == 1) {
				field.get(0).setAccessible(true);
				field.get(0).set(obj, obj);
			}
			else if (field.size() > 1) {
				logger.error("{} not registered, contains more than one @Instance annotation", module.getName());
				return;
			}

			Arrays.stream(module.getDeclaredMethods()).filter(x -> x.isAnnotationPresent(EventHandler.class)).forEach(x -> {
				if (x.getParameterTypes().length != 1) {
					logger.warn("Event Handlers can only have 1 parameter, {}.{} has {}", module.getName(), x.getName(), x.getParameterTypes().length);
				}
				else {
					this.addInvocation(x.getParameterTypes()[0].asSubclass(IEvent.class), EventInvocation.of(x, obj));
				}

			});
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		catch (NoClassDefFoundError e) {
			logger.warn("{} not registered, could not find the required class dependency '{}'", module.getName(), e.getMessage());
		}
	}

	void removeInvocationsOfEvent(Class<? extends IEvent> event) {
		this.eventMap.remove(event);
	}

	private void addInvocation(Class<? extends IEvent> event, EventInvocation invocation) {
		if (!this.eventMap.containsKey(event)) {
			this.eventMap.put(event, new LinkedList<>());
		}
		this.eventMap.get(event).add(invocation);
	}

	static class EventInvocation extends Tuple<Method, Object> {

		EventInvocation(Method method, Object obj) {
			super(method, obj);
		}

		static EventInvocation of(Method method, Object obj) {
			return new EventInvocation(method, obj);
		}

		void invoke(IEvent event) {
			try {
				this.getKey().invoke(this.getValue(), event);
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

}
