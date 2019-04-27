package uk.ac.warwick.dcs.sherlock.api.executor;

import uk.ac.warwick.dcs.sherlock.api.component.IJob;

import java.util.*;

public interface IExecutor {

	List<IJobStatus> getAllJobStatuses();

	List<IJob> getWaitingJobs();

	IJobStatus getJobStatus(IJob job);

	IJob getJob(IJobStatus jobStatus);

	void shutdown();

	boolean submitJob(IJob job);

	boolean dismissJob(IJobStatus jobStatus);

	boolean dismissJob(IJob job);

	boolean cancelJob(IJobStatus jobStatus);

	boolean cancelJob(IJob job);

}
