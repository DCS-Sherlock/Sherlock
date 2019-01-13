package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import java.util.concurrent.*;

public class PriorityWorkTask {

	private RecursiveAction topAction;
	private PriorityWorkPriorities priority;

	public PriorityWorkTask(RecursiveAction topAction, PriorityWorkPriorities priority) {
		this.topAction = topAction;
		this.priority = priority;
	}

	RecursiveAction getTopAction() {
		return topAction;
	}

	PriorityWorkPriorities getPriority() {
		return priority;
	}
}
