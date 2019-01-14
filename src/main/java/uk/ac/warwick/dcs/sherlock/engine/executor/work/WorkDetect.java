package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector.IDetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class WorkDetect extends RecursiveTask<List<AbstractModelTaskRawResult>> {

	private List<IDetectorWorker> workers;
	private int threshold;
	private int begin;
	private int end;

	public WorkDetect(List<IDetectorWorker> workers, int threshold) {
		this(workers, threshold, 0, workers.size());
	}

	private WorkDetect(List<IDetectorWorker> workers, int threshold, int begin, int end) {
		this.workers = workers;
		this.threshold = threshold;
		this.begin = begin;
		this.end = end;
	}

	@Override
	protected List<AbstractModelTaskRawResult> compute() {
		int size = this.end - this.begin;

		if (size > this.threshold) {
			int middle = this.begin + (size / 2);
			WorkDetect t1 = new WorkDetect(this.workers, this.threshold, this.begin, middle);
			t1.fork();
			WorkDetect t2 = new WorkDetect(this.workers,this.threshold,  middle, this.end);

			List<AbstractModelTaskRawResult> res = t2.compute();
			res.addAll(t1.join());

			return res;
		}
		else {
			return this.workers.stream().map(WorkDetect::runWorker).collect(Collectors.toList());
		}
	}

	private static AbstractModelTaskRawResult runWorker(IDetectorWorker worker) {
		worker.execute();
		return worker.getRawResult();
	}
}
