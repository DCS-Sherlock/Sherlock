package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import java.util.concurrent.*;

public interface IPriorityWorkSchedulerWrapper {

	/**
	 * Blocking execution of the work, returns when work is complete
	 * @param topAction top level recursive action to fork
	 * @param priority work priority level
	 */
	void invokeWork(ForkJoinTask topAction, Priority priority);

	/**
	 * Non-blocking, submit the task to the work executor
	 * @param task task object to execute
	 */
	void submitWork(PriorityWorkTask task);

}
