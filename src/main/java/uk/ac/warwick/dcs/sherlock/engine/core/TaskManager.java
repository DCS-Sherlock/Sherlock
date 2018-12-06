package uk.ac.warwick.dcs.sherlock.engine.core;

import uk.ac.warwick.dcs.sherlock.api.annotation.RequestProcessor;
import uk.ac.warwick.dcs.sherlock.api.annotation.RequestProcessor.Instance;

/**
 * Class which handles the execution of Sherlock tasks
 */
@RequestProcessor (apiFieldName = "taskManager")
public class TaskManager {

	@Instance
	static TaskManager instance;

	TaskManager() {

	}


}
