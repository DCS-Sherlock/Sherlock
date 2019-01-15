package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import java.util.concurrent.*;

public class PriorityWorkTask {

	private ForkJoinTask topAction;
	private PriorityWorkPriorities priority;

	public PriorityWorkTask(ForkJoinTask topAction, PriorityWorkPriorities priority) {
		this.topAction = topAction;
		this.priority = priority;
	}

	ForkJoinTask getTopAction() {
		return topAction;
	}

	PriorityWorkPriorities getPriority() {
		return priority;
	}
}
