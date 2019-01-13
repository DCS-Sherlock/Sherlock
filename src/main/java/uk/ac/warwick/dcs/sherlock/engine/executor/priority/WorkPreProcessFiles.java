package uk.ac.warwick.dcs.sherlock.engine.executor.priority;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class WorkPreProcessFiles extends RecursiveAction {

	private List<ITask> tasks;

	private ISourceFile file;
	private List<ISourceFile> files;

	private WorkPreProcessFiles(List<ITask> tasks, ISourceFile file) {
		this.tasks = tasks;

		this.file = file;
		this.files = null;
	}

	WorkPreProcessFiles(List<ITask> tasks, List<ISourceFile> files) {
		this.tasks = tasks;

		this.file = null;
		this.files = files;
	}

	@Override
	protected void compute() {
		if (this.file != null) {
			String content = this.file.getFileContentsAsString();
			ForkJoinTask.invokeAll(this.tasks.stream().map(x -> new WorkPreProcessFile(x, content)).collect(Collectors.toList()));
		}
		else {
			ForkJoinTask.invokeAll(this.files.stream().map(x -> new WorkPreProcessFiles(this.tasks, x)).collect(Collectors.toList()));
		}
	}
}
