package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import java.util.concurrent.*;

public class PriorityWorkTask {

	private ForkJoinTask topAction;
	private Priority priority;

	public PriorityWorkTask(ForkJoinTask topAction, Priority priority) {
		this.topAction = topAction;
		this.priority = priority;
	}

	ForkJoinTask getTopAction() {
		return topAction;
	}

	Priority getPriority() {
		return priority;
	}
}
