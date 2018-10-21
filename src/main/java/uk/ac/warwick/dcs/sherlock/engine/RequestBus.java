package uk.ac.warwick.dcs.sherlock.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotations.RequestProcessor;
import uk.ac.warwick.dcs.sherlock.api.request.IRequestBus;
import uk.ac.warwick.dcs.sherlock.api.request.IRequestReference;
import uk.ac.warwick.dcs.sherlock.api.request.RequestInvocation;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

class RequestBus implements IRequestBus {

	final Logger logger = LoggerFactory.getLogger(RequestBus.class);
	private Map<Class<?>, RequestInvocation> processorMap;
	private Map<Class<?>, Method> responseMap;
	private Class<?>[] requestHandlerParamTypes = { IRequestReference.class, RequestInvocation.class, Object.class };
	private Class<?>[] responceHandlerParamTypes = { IRequestReference.class, Object.class };

	RequestBus() {
		this.processorMap = new ConcurrentHashMap<>();
		this.responseMap = new ConcurrentHashMap<>();
	}

	@Override
	public boolean post(@NotNull IRequestReference reference, @NotNull Object source, Object payload) {
		if (reference == null || source == null) {
			logger.error("Cannot post request with a null reference or source");
		}

		if (reference.getHandler() != null) {
			RequestInvocation invocation = this.processorMap.get(reference.getHandler());
			RequestInvocation responseInvocation = null;

			if (this.responseMap.containsKey(source.getClass())) {
				responseInvocation = RequestInvocation.of(this.responseMap.get(source.getClass()), source);
			}
			else {
				logger.info("{} has no valid request response handler", source.getClass());
			}

			if (invocation != null) {
				return invocation.post(reference, responseInvocation, payload);
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
				field.set(null, processor);
			}

			Object obj = processor.newInstance();

			List<Field> field = Arrays.stream(processor.getFields()).filter(x -> x.isAnnotationPresent(RequestProcessor.Instance.class)).collect(Collectors.toList());
			if (field.size() == 1) {
				field.get(0).setAccessible(true);
				field.get(0).set(obj, obj);
			}
			else if (field.size() > 1) {
				logger.error("{} not registered, contains more than one @Instance annotation", processor.getName());
				return;
			}

			List<Method> m = Arrays.stream(processor.getMethods()).filter(x -> x.isAnnotationPresent(RequestProcessor.PostHandler.class)).collect(Collectors.toList());
			if (m.size() == 1) {
				if (Arrays.equals(m.get(0).getParameterTypes(), requestHandlerParamTypes)) {
					this.processorMap.put(processor, RequestInvocation.of(m.get(0), obj));
				}
				else {
					logger.error("{} @PostHandler method does not have valid parameter types, they should be [IRequestReference, RequestInvocation, Object]", processor.getName());
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
			logger.error(String.format("Could not find field matching 'apiFieldName' in the database class for %s", processor.getName()), e);
		}
	}

	void registerResponseHandler(Method method) {
		if (Arrays.equals(method.getParameterTypes(), responceHandlerParamTypes)) {
			this.responseMap.put(method.getDeclaringClass(), method);
		}
		else {
			logger.error("{} does not have valid parameter types, @ResponceHandler methods should have the params [IRequestReference, Object, Object]", method.toString());
		}
	}

}
