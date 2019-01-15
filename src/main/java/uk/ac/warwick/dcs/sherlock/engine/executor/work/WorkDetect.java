package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector.IDetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

import java.util.*;
import java.util.concurrent.*;

public class WorkDetect extends RecursiveTask<List<AbstractModelTaskRawResult>> {

	private List<IDetectorWorker> workers;
	private int threshold;
	private int begin;
	private int end;

	private List<AbstractModelTaskRawResult> result;

	public WorkDetect(List<IDetectorWorker> workers, int threshold) {
		this(workers, threshold, 0, workers.size());
		this.result = Collections.EMPTY_LIST;
	}

	private WorkDetect(List<IDetectorWorker> workers, int threshold, int begin, int end) {
		this.workers = workers;
		this.threshold = threshold;
		this.begin = begin;
		this.end = end;
		this.result = null;
	}

	@Override
	protected List<AbstractModelTaskRawResult> compute() {
		List<AbstractModelTaskRawResult> res;
		int size = this.end - this.begin;

		if (size > this.threshold) {
			int middle = this.begin + (size / 2);
			WorkDetect t1 = new WorkDetect(this.workers, this.threshold, this.begin, middle);
			t1.fork();
			WorkDetect t2 = new WorkDetect(this.workers,this.threshold,  middle, this.end);

			res = t2.compute();
			res.addAll(t1.join());
		}
		else {
			res = new LinkedList<>();

			for (int i = this.begin; i < this.end; i++) {
				AbstractModelTaskRawResult raw = runWorker(this.workers.get(i));
				if (raw != null) {
					res.add(raw);
				}
			}
		}

		if (this.result != null) {
			this.result = res;
		}

		return res;
	}

	public List<AbstractModelTaskRawResult> getResults() {
		return this.result;
	}

	private static AbstractModelTaskRawResult runWorker(IDetectorWorker worker) {
		worker.execute();
		return worker.getRawResult();
	}
}
