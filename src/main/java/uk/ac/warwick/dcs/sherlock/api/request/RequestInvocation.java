package uk.ac.warwick.dcs.sherlock.api.request;

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

	public boolean post(IRequestReference reference, RequestInvocation source, Object payload) {
		try {
			this.getKey().invoke(this.getValue(), reference, source, payload);
			return true;
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean respond(IRequestReference reference, Object responce) {
		try {
			this.getKey().invoke(this.getValue(), reference, responce);
			return true;
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
}
