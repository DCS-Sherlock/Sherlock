package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.util.SherlockHelper;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.api.executor.IExecutor;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Various executor utilities
 */
public class ExecutorUtils {

	public static final Logger logger = LoggerFactory.getLogger(IExecutor.class);

	/**
	 * does average of list
	 * @param scores list to average
	 * @return average
	 */
	public static float aggregateScores(Collection<Float> scores) {
		return (float) scores.stream().mapToDouble(x -> x).average().orElse(-1);
	}

	/**
	 * Populates the adjustables in an object
	 * @param instance object to populate
	 * @param params list of param references and values
	 * @param <T> type of the object to populate
	 */
	public static <T> void processAdjustableParameters(T instance, Map<String, Float> params) {
		Arrays.stream(instance.getClass().getDeclaredFields()).map(f -> new Tuple<>(f, f.getDeclaredAnnotationsByType(AdjustableParameter.class))).filter(x -> x.getValue().length == 1).forEach(x -> {
			String ref = SherlockHelper.buildFieldReference(x.getKey());
			boolean isInt = x.getKey().getType().equals(int.class);
			float val;

			if (params.containsKey(ref)) {
				val = params.get(ref);

				if (isInt && val % 1 != 0) {
					synchronized (logger) {
						logger.error("Trying to assign a float value to integer adjustable parameter {}", ref);
					}
					return;
				}

				if (val > x.getValue()[0].maxumumBound() || val < x.getValue()[0].minimumBound()) {
					synchronized (logger) {
						logger.error("Trying to assign an out of bounds value to adjustable parameter {}", ref);
					}
					return;
				}
			}
			else {
				val = x.getValue()[0].defaultValue();
			}

			Field f = x.getKey();
			f.setAccessible(true);
			try {
				if (isInt) {
					int vali = (int) val;
					f.set(instance, vali);
				}
				else {
					f.set(instance, val);
				}
			}
			catch (IllegalAccessException | IllegalArgumentException | NullPointerException e) {
				synchronized (logger) {
					logger.error("Could not set adjustable parameter", e);
				}
			}
		});
	}

}
