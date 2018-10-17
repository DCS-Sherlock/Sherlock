package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.model.data.IModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelResultItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An abstract IDetector implementation which constructs an individual, parallel worker for each combination
 * of files in the dataset. 
 */
public abstract class AbstractPairwiseDetector implements IDetector {

	private static <E> Stream<List<E>> combinations(List<E> l, int size) {
		if (size == 0) {
			return Stream.of(Collections.emptyList());
		}
		else {
			return IntStream.range(0, l.size()).boxed().flatMap(i -> combinations(l.subList(i + 1, l.size()), size - 1).map(t -> pipe(l.get(i), t)));
		}
	}

	private static <E> List<E> pipe(E head, List<E> tail) {
		List<E> newList = new ArrayList<>(tail);
		newList.add(0, head);
		return newList;
	}

	@Override
	public List<IDetectorWorker> buildWorkers(List<IModelDataItem> data, Class<? extends IModelResultItem> resultItemClass) {
		return combinations(data, 2).map(x -> this.getAbstractPairwiseDetectorWorker().putData(x.get(0), x.get(1), resultItemClass)).collect(Collectors.toList());
	}

	public abstract AbstractPairwiseDetectorWorker getAbstractPairwiseDetectorWorker();

	public abstract class AbstractPairwiseDetectorWorker implements IDetectorWorker {

		protected IModelDataItem file1;
		protected IModelDataItem file2;
		protected IModelResultItem result;

		@Override
		public IModelResultItem getResult() {
			return this.result;
		}

		AbstractPairwiseDetectorWorker putData(IModelDataItem file1Data, IModelDataItem file2Data, Class<? extends IModelResultItem> resultItemClass) {
			this.file1 = file1Data;
			this.file2 = file2Data;

			try {
				this.result = resultItemClass.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}

			return this;
		}
	}

}
