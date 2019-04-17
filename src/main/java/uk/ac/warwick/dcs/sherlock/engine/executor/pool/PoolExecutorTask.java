package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.ModelTaskProcessedResults;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;
import uk.ac.warwick.dcs.sherlock.engine.component.WorkStatus;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorUtils;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.JobStatus;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.Priority;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.IWorkTask;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.WorkDetect;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class PoolExecutorTask implements Callable<ModelTaskProcessedResults>, IWorkTask {

	List<ModelDataItem> dataItems;
	int callType;

	private IPriorityWorkSchedulerWrapper scheduler;
	private JobStatus status;

	private ITask task;
	private String language;
	private List<PreProcessingStrategy> preProcessingStrategies;

	private List<DetectorWorker> workers;
	private IDetector detector;

	PoolExecutorTask(JobStatus jobStatus, IPriorityWorkSchedulerWrapper scheduler, ITask task, String language) {
		this.callType = 1;
		this.status = jobStatus;
		this.scheduler = scheduler;
		this.task = task;
		this.language = language;

		this.dataItems = Collections.synchronizedList(new LinkedList<>());
		this.workers = null;

		try {
			IDetector instance = task.getDetector().getConstructor().newInstance();
			this.preProcessingStrategies = instance.getPreProcessors();
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addModelDataItem(ModelDataItem item) {
		this.dataItems.add(item);
	}

	@Override
	public ModelTaskProcessedResults call() {
		if (this.callType == 1) {
			this.build();
		}
		else if (this.callType == 2) {
			this.runDetector();
		}
		else if (this.callType == 3) {
			return this.runPostProcessing();
		}

		return null;
	}

	@Override
	public Class<? extends IDetector> getDetector() {
		return this.task.getDetector();
	}

	@Override
	public String getLanguage() {
		return this.language;
	}

	@Override
	public List<PreProcessingStrategy> getPreProcessingStrategies() {
		return preProcessingStrategies;
	}

	public WorkStatus getStatus() {
		return this.task.getStatus();
	}

	@Override
	public JobStatus getJobStatus() {
		return this.status;
	}

	public ITask getTask() {
		return task;
	}

	void build() {
		try {
			this.detector = this.task.getDetector().getConstructor().newInstance();
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			return;
		}

		ExecutorUtils.processAdjustableParameters(this.detector, this.task.getParameterMapping());

		try {
			this.workers = this.detector.buildWorkers(this.dataItems);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (this.workers.size() == 0) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Error building detector {}, no workers were built", this.getDetector().getName());
			}
		}

		this.status.incrementProgress();
		this.callType = 2;
	}

	int getWorkerSize() {
		return this.workers.size();
	}

	private void runDetector() {
		try {

			int threshold = Math.min(Math.max(this.workers.size() / Runtime.getRuntime().availableProcessors(), 1), 4); //set min and max num workers in a thread

			WorkDetect detect = new WorkDetect(this.status, this.workers, threshold);
			this.scheduler.invokeWork(detect, Priority.DEFAULT);
			List<AbstractModelTaskRawResult> rawResults = detect.getResults();

			if (this.workers.size() != rawResults.size()) {
				synchronized (ExecutorUtils.logger) {
					ExecutorUtils.logger.error("Error running workers, got {} results from {} workers", rawResults.size(), this.workers.size());
					return;
				}
			}

			rawResults = rawResults.stream().filter(Objects::nonNull).filter(x -> !x.isEmpty()).collect(Collectors.toList());
			if (rawResults.size() > 0) {

				// validate the raw result types, are they all the same?
				AbstractModelTaskRawResult base = rawResults.get(0);
				if (!rawResults.stream().allMatch(x -> x.testType(base))) {
					synchronized (ExecutorUtils.logger) {
						ExecutorUtils.logger.error("Work result types are not consistent, this is not allowed. A detector must return a single result type");
						return;
					}
				}

				//Save the raw results
				this.task.setRawResults(rawResults);
			}

			this.task.setComplete();
			this.callType = 3;
		}
		catch (Exception e) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Error running task", e);
			}
		}
	}

	private ModelTaskProcessedResults runPostProcessing() {
		if (this.task.getStatus() == WorkStatus.COMPLETE) {
			List<AbstractModelTaskRawResult> rawResults = task.getRawResults();
			if (rawResults != null && rawResults.size() > 0) {
				try {
					IPostProcessor postProcessor = SherlockRegistry.getPostProcessorInstance(rawResults.get(0).getClass());
					if (postProcessor == null) {
						synchronized (ExecutorUtils.logger) {
							ExecutorUtils.logger.error("Could not find a postprocessor for '{}', check that it is being correctly registered", rawResults.get(0).getClass().getName());
							return null;
						}
					}

					ExecutorUtils.processAdjustableParameters(postProcessor, this.task.getParameterMapping());
					ModelTaskProcessedResults processedResults = postProcessor.processResults(this.task.getJob().getWorkspace().getFiles(), rawResults);
					try {
						if (processedResults.cleanGroups()) {
							synchronized (ExecutorUtils.logger) {
								ExecutorUtils.logger.warn("At least one result group for job {} [task {}] does not have it's detection type set, results will be ignored", this.getTask().getJob().getPersistentId(),
										this.getTask().getPersistentId());
							}
							return null;
						}
					}
					catch (UnknownDetectionTypeException e) {
						synchronized (ExecutorUtils.logger) {
							ExecutorUtils.logger.warn("At least one result group for job {} [task {}] has an unknown detection type set", this.getTask().getJob().getPersistentId(),
									this.getTask().getPersistentId());
						}
						e.printStackTrace();
						return null;
					}
					this.status.incrementProgress();

					return processedResults;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Trying to post process incomplete task");
			}
		}

		return null;
	}
}

