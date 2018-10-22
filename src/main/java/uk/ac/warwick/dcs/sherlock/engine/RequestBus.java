package uk.ac.warwick.dcs.sherlock.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotations.RequestProcessor;
import uk.ac.warwick.dcs.sherlock.api.request.IRequestBus;
import uk.ac.warwick.dcs.sherlock.api.request.Request;
import uk.ac.warwick.dcs.sherlock.api.request.RequestInvocation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

class RequestBus implements IRequestBus {

	final Logger logger = LoggerFactory.getLogger(RequestBus.class);
	private Map<Class<?>, RequestInvocation> processorMap;
	private Map<Class<?>, Method> responseMap;
	private Class<?>[] paramTypes = { Request.class };

	RequestBus() {
		this.processorMap = new ConcurrentHashMap<>();
		this.responseMap = new ConcurrentHashMap<>();
	}

	@Override
	public Request post(Request reference) {
		if (reference == null) {
			logger.error("Cannot post request with a null reference");
			return null;
		}

		if (reference.getHandler() != null) {
			RequestInvocation invocation = this.processorMap.get(reference.getHandler());

			if (invocation != null) {
				return invocation.post(reference);
			}
			else {
				logger.warn("Could not find invocation for {}, is it annotated with @RequestProcessor?", reference.getHandler());
			}
		}
		else {
			logger.warn("{} handler class is null, likely an earlier registration failure occurred");
		}
		return null;
	}

	@Override
	public boolean post(Request reference, Object source) {
		if (reference == null || source == null) {
			logger.error("Cannot post request with a null reference or source");
			return false;
		}

		if (reference.getHandler() != null) {
			RequestInvocation invocation = this.processorMap.get(reference.getHandler());

			if (invocation != null) {
				Runnable runnable = () -> {
					if (this.responseMap.containsKey(source.getClass())) {
						RequestInvocation responseInvocation = RequestInvocation.of(this.responseMap.get(source.getClass()), source);

						Request res = invocation.post(reference);
						responseInvocation.respond(res);
					}
					else {
						logger.info("{} has no valid request response handler", source.getClass());
					}
				};
				runnable.run();
			}
			else {
				logger.warn("Could not find invocation for {}, is it annotated with @RequestProcessor?", reference.getHandler());
			}
		}
		else {
			logger.warn("{} handler class is null, likely an earlier registration failure occurred");
		}
		return false;
	}

	void registerProcessor(Class<?> processor) {
		try {
			String apiFieldName = processor.getAnnotation(RequestProcessor.class).apiFieldName();
			if (!apiFieldName.equals("")) {
				Field field = processor.getAnnotation(RequestProcessor.class).databaseClass().getDeclaredField(apiFieldName);
				field.setAccessible(true);
				if (field.get(null) == null) {
					field.set(null, processor);
				}
				else {
					logger.error("Failed to register the {} request processor, field matching '{}' in '{}'  already contains a value", processor.getName(),
							processor.getAnnotation(RequestProcessor.class).apiFieldName(), processor.getAnnotation(RequestProcessor.class).databaseClass().getName());
				}
			}

			Object obj = processor.newInstance();

			List<Field> field = Arrays.stream(processor.getDeclaredFields()).filter(x -> x.isAnnotationPresent(RequestProcessor.Instance.class)).collect(Collectors.toList());
			if (field.size() == 1) {
				field.get(0).setAccessible(true);

				if (field.get(0).get(null) != null) {
					obj = field.get(0).get(null);
					logger.info("Use existing instance of: {}", processor.getName());
				}
				else {
					field.get(0).set(obj, obj);
				}
			}
			else if (field.size() > 1) {
				logger.error("{} not registered, contains more than one @Instance annotation", processor.getName());
				return;
			}

			List<Method> m = Arrays.stream(processor.getDeclaredMethods()).filter(x -> x.isAnnotationPresent(RequestProcessor.PostHandler.class)).collect(Collectors.toList());
			if (m.size() == 1) {
				if (!Arrays.equals(m.get(0).getParameterTypes(), paramTypes)) {
					logger.error("{} @PostHandler method does not have valid parameter types, they should be [Request]", processor.getName());
				}
				else if (m.get(0).getReturnType().getSuperclass().equals(Request.class)) {
					logger.error("{} @PostHandler method should return the request result", processor.getName());
				}
				else {
					this.processorMap.put(processor, RequestInvocation.of(m.get(0), obj));
				}
			}
			else if (m.size() > 1) {
				logger.error("{} not registered, contains more than one @PostHandler annotation", processor.getName());
			}
		}
		catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		catch (NoSuchFieldException e) {
			logger.error(String.format("Failed to register the %s request processor, could not find field matching '%s' in '%s'", processor.getName(),
					processor.getAnnotation(RequestProcessor.class).apiFieldName(), processor.getAnnotation(RequestProcessor.class).databaseClass().getName()), e);
		}
	}

	void registerResponseHandler(Method method) {
		if (Arrays.equals(method.getParameterTypes(), paramTypes)) {
			this.responseMap.put(method.getDeclaringClass(), method);
		}
		else {
			logger.error("{} does not have valid parameter types, @ResponceHandler methods should have the params [Request]", method.toString());
		}
	}

}
