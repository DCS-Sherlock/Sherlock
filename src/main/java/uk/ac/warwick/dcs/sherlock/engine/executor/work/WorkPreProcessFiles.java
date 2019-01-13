package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class WorkPreProcessFiles extends RecursiveAction {

	private List<IWorkTask> tasks;

	private ISourceFile file;
	private List<ISourceFile> files;

	private WorkPreProcessFiles(List<IWorkTask> tasks, ISourceFile file) {
		this.tasks = tasks;
		this.file = file;
		this.files = null;
	}

	public WorkPreProcessFiles(List<IWorkTask> tasks, List<ISourceFile> files) {
		this.tasks = tasks;
		this.file = null;
		this.files = files;
	}

	@Override
	protected void compute() {
		if (this.file != null) {
			ForkJoinTask.invokeAll(this.tasks.stream().map(x -> new WorkPreProcessFile(x, this.file, this.file.getFileContentsAsString())).collect(Collectors.toList()));
		}
		else {
			ForkJoinTask.invokeAll(this.files.stream().map(x -> new WorkPreProcessFiles(this.tasks, x)).collect(Collectors.toList()));
		}
	}
}
