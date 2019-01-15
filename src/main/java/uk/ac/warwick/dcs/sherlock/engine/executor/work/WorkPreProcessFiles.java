package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;
import java.util.concurrent.*;

public class WorkPreProcessFiles extends RecursiveAction {

	private List<IWorkTask> tasks;

	private List<ISourceFile> files;
	private int begin;
	private int end;

	public WorkPreProcessFiles(List<IWorkTask> tasks, List<ISourceFile> files) {
		this(tasks,files, 0, files.size());
	}

	private WorkPreProcessFiles(List<IWorkTask> tasks, List<ISourceFile> files, int begin, int end) {
		this.tasks = tasks;

		this.files = files;
		this.begin = begin;
		this.end = end;
	}

	@Override
	protected void compute() {
		int size = this.end - this.begin;

		if (size > 1) {
			int middle = this.begin + (size / 2);
			WorkPreProcessFiles t1 = new WorkPreProcessFiles(this.tasks, this.files, this.begin, middle);
			t1.fork();
			WorkPreProcessFiles t2 = new WorkPreProcessFiles(this.tasks, this.files, middle, this.end);
			t2.compute();
			t1.join();
		}
		else {
			WorkPreProcessFile f1 = new WorkPreProcessFile(tasks, this.files.get(this.begin));
			f1.compute();
		}
	}
}
