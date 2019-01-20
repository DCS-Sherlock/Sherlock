package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.model.detection.AbstractDetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.ModelTaskProcessedResults;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorUtils;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.Priority;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.IWorkTask;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.WorkDetect;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class PoolExecutorTask implements Callable<Void>, IWorkTask {

	List<ModelDataItem> dataItems;
	private IPriorityWorkSchedulerWrapper scheduler;
	private ITask task;
	private String language;
	private List<PreProcessingStrategy> preProcessingStrategies;

	PoolExecutorTask(IPriorityWorkSchedulerWrapper scheduler, ITask task, String language) {
		this.scheduler = scheduler;
		this.task = task;
		this.language = language;

		this.dataItems = Collections.synchronizedList(new LinkedList<>());

		try {
			IDetector instance = task.getDetector().newInstance();
			this.preProcessingStrategies = instance.getPreProcessors();
		}
		catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addModelDataItem(ModelDataItem item) {
		this.dataItems.add(item);
	}

	@Override
	public Void call() throws IllegalAccessException, InstantiationException {
		IDetector instance = this.task.getDetector().newInstance();
		ExecutorUtils.processAdjustableParameters(instance, this.task.getParameterMapping());

		List<AbstractDetectorWorker> workers = instance.buildWorkers(this.dataItems);
		int threshold = Math.min(Math.max(workers.size() / Runtime.getRuntime().availableProcessors(), 2), 6); //set min and max num workers in a thread

		WorkDetect detect = new WorkDetect(workers, threshold);
		this.scheduler.invokeWork(detect, Priority.DEFAULT);
		List<AbstractModelTaskRawResult> rawResults = detect.getResults();

		if (workers.size() != rawResults.size()) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Error running workers, got {} results from {} workers", rawResults.size(), workers.size());
				return null;
			}
		}

		rawResults = rawResults.stream().filter(Objects::nonNull).filter(x -> !x.isEmpty()).collect(Collectors.toList());
		if (rawResults.size() > 0) {

			// validate the raw result types, are they all the same?
			AbstractModelTaskRawResult base = rawResults.get(0);
			if (!rawResults.stream().allMatch(x -> x.testType(base))) {
				synchronized (ExecutorUtils.logger) {
					ExecutorUtils.logger.error("Work result types are not consistent, this is not allowed. A detector must return a single result type");
					return null;
				}
			}

			//Save the raw results
			this.task.setRawResults(rawResults);

			// TODO: TEMP BELOW HERE, MOVE BACK TO THE JOB TO ALLOW FOR REPROCESSING
			try {
				IPostProcessor postProcessor = SherlockRegistry.getPostProcessorInstance(rawResults.get(0).getClass());
				if (postProcessor == null) {
					synchronized (ExecutorUtils.logger) {
						ExecutorUtils.logger.error("Could not find a postprocessor for '{}', check that it is being correctly registered", rawResults.get(0).getClass().getName());
						return null;
					}
				}
				ModelTaskProcessedResults processedResults = postProcessor.processResults(this.task.getJob().getWorkspace().getFiles(), rawResults);

				List<ICodeBlockGroup> gs = processedResults.getGroups();
				synchronized (ExecutorUtils.logger) {
					ExecutorUtils.logger.warn("Found {} groups:\n", gs.size());
					for (ICodeBlockGroup g : gs) {
						ExecutorUtils.logger.warn("{}", g.getComment());
						ExecutorUtils.logger.warn("==================");
						g.getCodeBlocks().forEach(x -> ExecutorUtils.logger.warn("{} - {}", x.getFile(), x.getLineNumbers().toString()));
						System.out.println();
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			// END TEMP
		}
		else {
			this.task.setComplete();
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
}

