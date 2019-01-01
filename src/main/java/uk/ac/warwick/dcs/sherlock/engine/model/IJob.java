package uk.ac.warwick.dcs.sherlock.engine.model;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;

import java.util.*;

public interface IJob {

	ITask createTask(IDetector detector);

	long getPersistentId();

	List<ITask> getTasks();

	IWorkspace getWorkspace();
}
