package uk.ac.warwick.dcs.sherlock.engine.model;

import java.util.*;

public interface IJob {

	ITask createTask();

	List<ITask> getTasks();

	IWorkspace getWorkspace();
}
