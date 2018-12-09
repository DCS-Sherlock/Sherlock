package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.model.data.ModelDataItem;

import java.util.*;
import java.util.stream.*;

/**
 * An abstract IDetector implementation which constructs an individual, parallel worker for each combination of files in the dataset. This can be used as a base for pairwise matching algorithms.
 * <p><p>
 * More advanced implementations should directly implement the IDetector interface.
 */
public abstract class AbstractPairwiseDetector implements IDetector {

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
	public List<IDetectorWorker> buildWorkers(List<ModelDataItem> data) {
		return combinations(data, 2).map(x -> this.getAbstractPairwiseDetectorWorker().putData(x.get(0), x.get(1), this.getPostProcessor())).collect(Collectors.toList());
	}

	/**
	 * @return a new instance of the worker for this implementation
	 */
	public abstract AbstractPairwiseDetectorWorker getAbstractPairwiseDetectorWorker();

	/**
	 * An extension of the basic worker for standard pairwise matching, implements the basic internal data structures
	 */
	public abstract class AbstractPairwiseDetectorWorker implements IDetectorWorker {

		protected ModelDataItem file1;
		protected ModelDataItem file2;
		protected AbstractPostProcessor result;

		/**
		 * Gets the results of the worker execution, only minimal processing should be performed in this method
		 *
		 * @return worker results
		 */
		@Override
		public AbstractPostProcessor getRawResult() {
			return this.result;
		}

		/**
		 * Loads data into the worker, called by the {@link AbstractPairwiseDetector#buildWorkers(List)} method
		 *
		 * @param file1Data       preprocessed data for file 1
		 * @param file2Data       preprocessed data for file 2
		 * @param postProcessorClass class the worker uses to return its results
		 *
		 * @return this (the current worker instance)
		 */
		AbstractPairwiseDetectorWorker putData(ModelDataItem file1Data, ModelDataItem file2Data, Class<? extends AbstractPostProcessor> postProcessorClass) {
			this.file1 = file1Data;
			this.file2 = file2Data;

			/*try {
				this.result = postProcessorClass.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}*/

			return this;
		}
	}

}
