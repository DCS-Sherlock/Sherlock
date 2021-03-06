package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import java.util.concurrent.*;

/**
 * Work task wrapper object
 */
public class PriorityWorkTask {

	private ForkJoinTask topAction;
	private Priority priority;

	public PriorityWorkTask(ForkJoinTask topAction, Priority priority) {
		this.topAction = topAction;
		this.priority = priority;
	}

	Priority getPriority() {
		return priority;
	}

	ForkJoinTask getTopAction() {
		return topAction;
	}
}
