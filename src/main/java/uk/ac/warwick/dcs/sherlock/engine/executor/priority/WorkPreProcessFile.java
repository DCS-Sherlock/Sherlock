package uk.ac.warwick.dcs.sherlock.engine.executor.priority;

import uk.ac.warwick.dcs.sherlock.engine.component.ITask;

import java.util.concurrent.*;

public class WorkPreProcessFile extends RecursiveAction {

	private ITask task;
	private String fileContent;

	WorkPreProcessFile(ITask task, String fileContent) {
		this.task = task;
		this.fileContent = fileContent;
	}

	@Override
	protected void compute() {

	}
}
