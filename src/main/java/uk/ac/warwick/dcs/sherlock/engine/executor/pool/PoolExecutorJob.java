package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.WorkStatus;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorUtils;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.JobStatus;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.Priority;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.WorkPreProcessFiles;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class PoolExecutorJob implements Runnable {

	private IPriorityWorkSchedulerWrapper scheduler;
	private IJob job;
	private JobStatus status;

	public PoolExecutorJob(IPriorityWorkSchedulerWrapper scheduler, IJob job, JobStatus status) {
		this.scheduler = scheduler;
		this.job = job;
		this.status = status;
	}

	public long getId() {
		return this.job.getPersistentId();
	}

	public Priority getPriority() {
		return this.status.getPriority();
	}

	public JobStatus getStatus() {
		return status;
	}

	@Override
	public void run() {
		if (!(job.getStatus() == WorkStatus.COMPLETE || job.getStatus() == WorkStatus.REGEN_RESULTS)) {
			job.setStatus(WorkStatus.ACTIVE);

			List<PoolExecutorTask> tasks = job.getTasks().stream().filter(x -> x.getStatus() != WorkStatus.COMPLETE).map(x -> new PoolExecutorTask(scheduler, x, job.getWorkspace().getLanguage()))
					.collect(Collectors.toList());

			RecursiveAction preProcess = new WorkPreProcessFiles(new ArrayList<>(tasks), this.job.getWorkspace().getFiles());
			this.scheduler.invokeWork(preProcess, Priority.DEFAULT);

			// Check that preprocessing went okay
			tasks.stream().filter(x -> x.dataItems.size() == 0).peek(x -> {
				synchronized (ExecutorUtils.logger) {
					ExecutorUtils.logger.error("PreProcessing output for detector {} is empty, this detector will be ignored.", x.getDetector().getName());
				}
			}).forEach(tasks::remove);

			ExecutorService exServ = Executors.newFixedThreadPool(tasks.size());
			try {
				exServ.invokeAll(new LinkedList<>(tasks));
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			job.setStatus(WorkStatus.REGEN_RESULTS);
		}

		//REGEN THE RESULTS
		/*IPostProcessor postProcessor = SherlockRegistry.getPostProcessorInstance(rawResults.get(0).getClass());
		if (postProcessor == null) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Could not find a postprocessor for '{}', check that it is being correctly registered", rawResults.get(0).getClass().getName());
				return null;
			}
		}
		ModelTaskProcessedResults processedResults = postProcessor.processResults(this.task.getJob().getWorkspace().getFiles(), rawResults);

		//TEMP CODE FROM HERE
		List<ICodeBlockGroup> gs = processedResults.getGroups();
		synchronized (ExecutorUtils.logger) {
			ExecutorUtils.logger.warn("Found {} groups:\n", gs.size());
			for (ICodeBlockGroup g : gs) {
				g.getCodeBlocks().forEach(x -> ExecutorUtils.logger.warn("{} - {}", x.getFile(), x.getLineNumbers().toString()));
				System.out.println();
			}
		}*/

		job.setStatus(WorkStatus.COMPLETE);
	}
}
