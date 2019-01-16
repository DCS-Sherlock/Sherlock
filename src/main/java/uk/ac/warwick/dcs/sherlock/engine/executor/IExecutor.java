package uk.ac.warwick.dcs.sherlock.engine.executor;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.JobStatus;

import java.util.*;

public interface IExecutor {

	boolean submitJob(IJob job);

	List<IJob> getCurrentJobs();

	JobStatus getJobStatus(IJob job);

	void shutdown();

}
