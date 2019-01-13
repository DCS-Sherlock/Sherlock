package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class WorkPreProcessFiles extends RecursiveAction {

	private List<IWorkTask> tasks;
	private Object file;

	private WorkPreProcessFiles(List<IWorkTask> tasks, ISourceFile file) {
		this.tasks = tasks;
		this.file = file;
	}

	public WorkPreProcessFiles(List<IWorkTask> tasks, List<ISourceFile> files) {
		this.tasks = tasks;
		this.file = files;
	}

	@Override
	protected void compute() {
		if (this.file instanceof ISourceFile) {
			ISourceFile f = (ISourceFile) this.file;
			String content = f.getFileContentsAsString();
			ForkJoinTask.invokeAll(this.tasks.stream().map(x -> new WorkPreProcessFile(x, f, content)).collect(Collectors.toList()));
		}
		else {
			ForkJoinTask.invokeAll(((List<ISourceFile>) this.file).stream().map(x -> new WorkPreProcessFiles(this.tasks, x)).collect(Collectors.toList()));
		}
	}
}
