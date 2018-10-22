package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestInvocation extends Tuple<Method, Object> {

	private RequestInvocation(Method method, Object obj) {
		super(method, obj);
	}

	public static RequestInvocation of(Method method, Object obj) {
		return new RequestInvocation(method, obj);
	}

	public Object post(IRequestReference reference, Object payload) {
		try {
			this.getKey().setAccessible(true);
			return this.getKey().invoke(this.getValue(), reference, payload);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void respond(IRequestReference reference, Object response) {
		try {
			this.getKey().setAccessible(true);
			this.getKey().invoke(this.getValue(), reference, response);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
