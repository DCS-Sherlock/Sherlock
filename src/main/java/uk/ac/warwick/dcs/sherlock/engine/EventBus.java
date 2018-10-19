package uk.ac.warwick.dcs.sherlock.engine;

import uk.ac.warwick.dcs.sherlock.api.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventSubscriber;
import uk.ac.warwick.dcs.sherlock.api.event.IEvent;
import uk.ac.warwick.dcs.sherlock.api.event.IEventBus;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.engine.lib.Reference;
import uk.ac.warwick.dcs.sherlock.launch.SherlockClient;
import uk.ac.warwick.dcs.sherlock.launch.SherlockServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class EventBus implements IEventBus {

	private Map<Class<? extends IEvent>, List<EventInvokation>> eventMap;

	EventBus() {
		this.eventMap = new HashMap<>();
	}

	private void addInvokation(Class<? extends IEvent> event, EventInvokation invokation) {
		if (!this.eventMap.containsKey(event)) {
			this.eventMap.put(event, new LinkedList<>());
		}
		this.eventMap.get(event).add(invokation);
	}

	@Override
	public void publishEvent(IEvent event) {
		this.eventMap.get(event.getClass()).parallelStream().forEach(x -> x.invoke(event));
	}

	@Override
	public void registerSubscriber(Class<?> subscriber) {
		Arrays.stream(subscriber.getMethods()).filter(x -> x.isAnnotationPresent(EventSubscriber.class)).forEach(x -> {
			System.out.println(x.getName());
		});
	}

	void registerModule(Class<?> module) {
		try {
			Object obj = module.newInstance();

			Arrays.stream(module.getMethods()).filter(x -> x.isAnnotationPresent(SherlockModule.EventHandler.class)).forEach(x -> {
				if (x.getParameterTypes().length != 1) {
					System.out.println("Wrong number of params");
				}
				else {
					if (!((module.equals(SherlockClient.class) && SherlockEngine.side != Reference.Side.CLIENT) || (module.equals(SherlockServer.class) && SherlockEngine.side != Reference.Side.SERVER))) {
						this.addInvokation(x.getParameterTypes()[0].asSubclass(IEvent.class), EventInvokation.of(x, obj));
					}
				}

			});
		}
		catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	void removeInvokationsOfEvent(Class<? extends IEvent> event) {
		this.eventMap.remove(event);
	}

	static class EventInvokation extends Tuple<Method, Object> {

		public EventInvokation(Method method, Object obj) {
			super(method, obj);
		}

		public void invoke(IEvent event) {
			try {
				this.getKey().invoke(this.getValue(), event);
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		public static EventInvokation of(Method method, Object obj) {
			return new EventInvokation(method, obj);
		}
	}

}
