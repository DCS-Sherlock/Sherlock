package uk.ac.warwick.dcs.sherlock.engine.model;

import java.util.*;

public interface IJob {

	//ITask createTask(AbstractDetector detector);

	/**
	 * Builds the tasks required for the job, it cannot be edited after this method is called
	 * @return successfully prepared?
	 */
	boolean prepare();

	/**
	 * @return has the prepare() method been called?
	 */
	boolean isPrepared();

	long getPersistentId();

	List<ITask> getTasks();

	IWorkspace getWorkspace();
}
