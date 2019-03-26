package uk.ac.warwick.dcs.sherlock.engine.executor;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.JobStatus;

import java.util.*;

public interface IExecutor {

	List<JobStatus> getAllJobStatuses();

	List<IJob> getWaitingJobs();

	JobStatus getJobStatus(IJob job);

	IJob getJob(JobStatus jobStatus);

	void shutdown();

	boolean submitJob(IJob job);

}
