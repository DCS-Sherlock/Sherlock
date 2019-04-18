/*package uk.ac.warwick.dcs.sherlock.engine.model;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.*;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy.GenericGeneralPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.executor.IExecutor;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.JobStatus;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardStringifier;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardTokeniser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/* TODO: temporary implementation*/
/*@Deprecated
public class TestResultsFactory implements IExecutor {

	@Override
	public List<IJob> getWaitingJobs() {
		return null;
	}

	@Override
	public JobStatus getJobStatus(IJob job) {
		return null;
	}

	@Override
	public void shutdown() {

	}

	@Override
	public boolean submitJob(IJob job) {
		try {
			this.buildTestResults(job);
		}
		catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}

		return true;
	}

	@SuppressWarnings ("Duplicates")
	@Deprecated
	private String buildTestResults(IJob job) throws IllegalAccessException, InstantiationException {
		if (!job.isPrepared()) {
			System.out.println("Could not run job, it is not prepared");
			return "Failed to run job, it is not prepared";
		}

		if (job.getTasks() == null || job.getTasks().isEmpty()) {
			System.out.println("Could not run job, has no tasks");
			return "Failed to run job, has no tasks";
		}

		IWorkspace workspace = job.getWorkspace();

		//ITask task = job.getTasks().get(0);
		for (ITask task : job.getTasks()) {
			IDetector instance = task.getDetector().newInstance();

			List<ISourceFile> files = workspace.getFiles();

			Class<? extends Lexer> lexerClass = instance.getLexer(workspace.getLanguage());
			Class<? extends Parser> parserClass = instance.getParser(workspace.getLanguage());

			List<PreProcessingStrategy> preProcessingStrategies = instance.getPreProcessors();
			/*String[] lexerChannels = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromString("")).getChannelNames();
			if (!preProcessingStrategies.stream().allMatch(x -> RegistryUtils.validatePreProcessingStrategy(x, lexerClass.getName(), lexerChannels))) {
				// strategy is not valid
				return null;
			}*/

			/*List<ModelDataItem> inputData = files.parallelStream().map(file -> {
				ConcurrentMap<String, List<IndexedString>> map = new ConcurrentHashMap<>();

				preProcessingStrategies.parallelStream().forEach(strategy -> {  //now with 100% more parallel [maybe don't run this in parallel if we have lots of files?]
					if (strategy.isAdvanced()) {
						if (strategy.getPreProcessorClasses().size() == 1) { //this is checked by the registry on startup
							try {
								ITuple<Class<? extends IAdvancedPreProcessor>, Class<? extends Lexer>> t = SherlockRegistry
										.getAdvancedPostProcessorForLanguage((Class<? extends IAdvancedPreProcessorGroup>) strategy.getPreProcessorClasses().get(0),
												workspace.getLanguage());

								Lexer lexer = t.getValue().getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromStream(file.getFileContents()));
								IAdvancedPreProcessor processor = t.getKey().newInstance();
								map.put(strategy.getName(), processor.process(lexer));
							}
							catch (InstantiationException | IllegalAccessException | NoSuchMethodException | IOException | InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
					else {
						try {
							Lexer lexerOld = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromStream(file.getFileContents()));
							List<? extends Token> tokensMaster = lexerOld.getAllTokens();

							List<? extends Token> tokens = new LinkedList<>(tokensMaster);
							for (Class<? extends IPreProcessor> processorClass : strategy.getPreProcessorClasses()) {
								try {
									IGeneralPreProcessor processor = (IGeneralPreProcessor) processorClass.newInstance();
									tokens = processor.process(tokens, lexerOld.getVocabulary(), workspace.getLanguage());
								}
								catch (InstantiationException | IllegalAccessException e) {
									e.printStackTrace();
								}
							}

							ITokenStringifier stringifier;
							if (strategy.getStringifier() != null) {
								stringifier = strategy.getStringifier();
							}
							else if (strategy instanceof GenericGeneralPreProcessingStrategy && ((GenericGeneralPreProcessingStrategy) strategy).isResultTokenised()) {
								stringifier = new StandardTokeniser();
							}
							else {
								stringifier = new StandardStringifier();
							}

							map.put(strategy.getName(), stringifier.processTokens(tokens, lexerOld.getVocabulary()));
						}
						catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				});

				return new ModelDataItem(file, map);
			}).collect(Collectors.toList());

			List<DetectorWorker> workers = instance.buildWorkers(inputData);
			workers.parallelStream().forEach(DetectorWorker::execute);

			/*List<AbstractModelTaskRawResult> raw = workers.stream().map(DetectorWorker::getRawResult).filter(x -> !x.isEmpty()).collect(Collectors.toList());
			boolean isValid = true;
			for (int i = 1; i < raw.size(); i++) {
				if (!raw.get(i).testType(raw.get(0))) {
					isValid = false;
				}
			}

			if (raw.size() == 0) {
				isValid = false;
			}

			if (!isValid) {
				System.out.println("Invalid raw result found");
				return "invalid";
			}

			task.setRawResults(raw);
			IPostProcessor postProcessor = SherlockRegistry.getPostProcessorInstance(raw.get(0).getClass());
			if (postProcessor == null) {
				System.out.println("Could not find a postprocessor for " + raw.get(0).getClass().getName() + ", check that it is being correctly registered!");
				return "bad postprocessor";
			}

			ModelTaskProcessedResults processedResults = postProcessor.processResults(files, raw);

			List<ICodeBlockGroup> gs = processedResults.getGroups();
			System.out.println("Found " + gs.size() + " groups:\n");
			for (ICodeBlockGroup g : gs) {
				g.getCodeBlocks().forEach(x -> System.out.println(x.getFile() + " - " + x.getLineNumbers().toString()));
				System.out.println();
			}*/
		/*}

		return "done"; //raw.stream().map(Objects::toString).collect(Collectors.joining("\n----\n"));
	}
}*/
