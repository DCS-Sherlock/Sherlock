package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.ModelTaskProcessedResults;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorUtils;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.PriorityWorkPriorities;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.IWorkTask;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.WorkDetect;

import java.util.*;
import java.util.concurrent.*;

public class PoolExecutorTask implements Callable<Void>, IWorkTask {

	private IPriorityWorkSchedulerWrapper scheduler;
	private ITask task;

	private Language language;
	private Class<? extends Lexer> lexerClass;
	private Class<? extends Parser> parserClass;
	private List<IPreProcessingStrategy> preProcessingStrategies;

	List<ModelDataItem> dataItems;

	PoolExecutorTask(IPriorityWorkSchedulerWrapper scheduler, ITask task, Language language) {
		this.scheduler = scheduler;
		this.task = task;
		this.language = language;

		this.dataItems = Collections.synchronizedList(new LinkedList<>());

		try {
			IDetector instance = task.getDetector().newInstance();
			this.lexerClass = instance.getLexer(language);
			this.parserClass = instance.getParser(language);
			this.preProcessingStrategies = instance.getPreProcessors(); // TODO: update this to be by language (for parser based stuff)
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
	public Language getLanguage() {
		return this.language;
	}

	@Override
	public Class<? extends Lexer> getLexerClass() {
		return this.lexerClass;
	}

	@Override
	public Class<? extends Parser> getParserClass() {
		return this.parserClass;
	}

	@Override
	public List<IPreProcessingStrategy> getPreProcessingStrategies() {
		return preProcessingStrategies;
	}

	@Override
	public Class<? extends IDetector> getDetector() {
		return this.task.getDetector();
	}

	@Override
	public Void call() throws IllegalAccessException, InstantiationException {
		IDetector instance = this.task.getDetector().newInstance();
		ExecutorUtils.processAdjustableParameters(instance, this.task.getParameterMapping());

		List<IDetector.IDetectorWorker> workers = instance.buildWorkers(this.dataItems);
		int threshold = Math.min(Math.max(workers.size()/Runtime.getRuntime().availableProcessors(), 2), 6); //set min and max num workers in a thread

		WorkDetect detect = new WorkDetect(workers, threshold);
		this.scheduler.invokeWork(detect, PriorityWorkPriorities.DEFAULT);
		List<AbstractModelTaskRawResult> rawResults = detect.getResults();

		if (workers.size() != rawResults.size()) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Error running workers, got {} results from {} workers", rawResults.size(), workers.size());
				return null;
			}
		}

		// validate the raw result types, are they all the same?
		if (!rawResults.stream().allMatch(x -> x.testType(rawResults.get(0)))) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Work result types are not consistent, this is not allowed. A detector must return a single result type");
				return null;
			}
		}

		//Save the raw results
		this.task.setRawResults(rawResults);

		IPostProcessor postProcessor = SherlockRegistry.getPostProcessorInstance(rawResults.get(0).getClass());
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
		}

		return null;
	}
}

