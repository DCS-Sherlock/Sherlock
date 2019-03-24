package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultJob;

import java.util.*;
import java.util.concurrent.*;

public class WorkScoreJob extends RecursiveAction {

	private List<ISourceFile> files;
	private int threshold;
	private int begin;
	private int end;

	private IResultJob result;

	public WorkScoreJob(IResultJob result, List<ISourceFile> files, int threshold) {
		this(result, files, threshold, 0, files.size());
	}

	private WorkScoreJob(IResultJob result, List<ISourceFile> files, int threshold, int begin, int end) {
		this.result = result;
		this.files = files;
		this.threshold = threshold;
		this.begin = begin;
		this.end = end;
	}

	private static void scoreFile(ISourceFile file) {
		// do file
	}

	@Override
	protected void compute() {
		int size = this.end - this.begin;

		if (size > this.threshold) {
			int middle = this.begin + (size / 2);
			WorkScoreJob t1 = new WorkScoreJob(this.result, this.files, this.threshold, this.begin, middle);
			t1.fork();
			WorkScoreJob t2 = new WorkScoreJob(this.result, this.files, this.threshold, middle, this.end);
			t2.compute();
			t1.join();
		}
		else {
			for (int i = this.begin; i < this.end; i++) {
				scoreFile(this.files.get(i));
			}
		}
	}
}
