package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorUtils;

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
		return combinations(data, 2).filter(x -> !x.get(0).getFile().getSubmission().equals(x.get(1).getFile().getSubmission())).map(x -> this.getAbstractPairwiseDetectorWorker(x.get(0), x.get(1))).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * Fetches a new instance of the worker for this implementation
	 *
	 * @return the new worker instance
	 */
	public T getAbstractPairwiseDetectorWorker(ModelDataItem file1Data, ModelDataItem file2Data) {

		try {
			try {
				return this.typeArgumentClass.getConstructor(IDetector.class, ModelDataItem.class, ModelDataItem.class).newInstance(this, file1Data, file2Data);
			}
			catch (NoSuchMethodException e) {
				return this.typeArgumentClass.getConstructor(this.getClass(), IDetector.class, ModelDataItem.class, ModelDataItem.class).newInstance(this, this, file1Data, file2Data);
			}
		}
		catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
			ExecutorUtils.logger.error("Could not build workers for detector {}. Ensure that the detector is not an inner class and its worker class {} has a constructor matching constructor(IDetector parent, ModelDataItem file1Data, ModelDataItem file2Data)", this.getClass().getName(), this.typeArgumentClass.getName());
		}

		return null;
	}
}
