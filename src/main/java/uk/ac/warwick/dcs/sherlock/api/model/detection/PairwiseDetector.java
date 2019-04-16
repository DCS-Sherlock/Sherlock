package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.*;

/**
 * An abstract IDetector implementation which constructs an individual, parallel worker for each combination of files in the dataset. This can be used as a base for pairwise matching algorithms.
 * <br><br>
 * More advanced implementations should directly implement the IDetector interface.
 */
public class PairwiseDetector<T extends PairwiseDetectorWorker> extends Detector<T> {

	private Class<T> typeArgumentClass;

	public PairwiseDetector(String displayName, Class<T> typeArgumentClass, PreProcessingStrategy... preProcessingStrategies) {
		this(displayName, "", typeArgumentClass, preProcessingStrategies);
	}

	public PairwiseDetector(String displayName, String description, Class<T> typeArgumentClass, PreProcessingStrategy... preProcessingStrategies) {
		super(displayName, description, preProcessingStrategies);

		this.typeArgumentClass = typeArgumentClass;
	}

	/**
	 * Recursively creates a list of all the possible combinations (unordered) of a specific size of an input list
	 *
	 * @param list list of items to find combinations of
	 * @param size size of the combinations, 2 returns pairs etc...
	 * @param <E>  list typing
	 *
	 * @return list of all possible combinations
	 */
	private static <E> Stream<List<E>> combinations(List<E> list, int size) {
		if (size == 0) {
			return Stream.of(Collections.emptyList());
		}
		else {
			return IntStream.range(0, list.size()).boxed().flatMap(i -> combinations(list.subList(i + 1, list.size()), size - 1).map(t -> pipe(list.get(i), t)));
		}
	}

	private static <E> List<E> pipe(E head, List<E> tail) {
		List<E> newList = new ArrayList<>(tail);
		newList.add(0, head);
		return newList;
	}

	@Override
	public final List<T> buildWorkers(List<ModelDataItem> data) {
		return (List<T>) combinations(data, 2).map(x -> {
			try {
				return this.getAbstractPairwiseDetectorWorker().putData(this, x.get(0), x.get(1));
			}
			catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * Fetches a new instance of the worker for this implementation
	 *
	 * @return the new worker instance
	 */
	public T getAbstractPairwiseDetectorWorker() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return typeArgumentClass.getConstructor().newInstance();
	}
}
