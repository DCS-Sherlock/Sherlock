package uk.ac.warwick.dcs.sherlock.engine.model;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.*;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IPreProcessingStrategy.GenericTokenPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardStringifier;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardTokeniser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/* TODO: temporary implementation*/
public class TestResultsFactory {

	public static String buildTestResults(IJob job) throws IllegalAccessException, InstantiationException {
		if (!job.isPrepared()) {
			System.out.println("Could not run job, it is not prepared");
			return "Failed to run job, it is not prepared";
		}

		if (job.getTasks() == null || job.getTasks().isEmpty()) {
			System.out.println("Could not run job, has no tasks");
			return "Failed to run job, has no tasks";
		}

		IWorkspace workspace = job.getWorkspace();
		ITask task = job.getTasks().get(0);
		IDetector instance = task.getDetector().newInstance();

		List<ISourceFile> files = workspace.getFiles();
		System.out.println(files.size());

		Class<? extends Lexer> lexerClass = instance.getLexer(workspace.getLanguage());
		Class<? extends Parser> parserClass = instance.getParser(workspace.getLanguage());

		List<IPreProcessingStrategy> preProcessingStrategies = instance.getPreProcessors();

		/*String[] lexerChannels = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromString("")).getChannelNames();
		if (!preProcessingStrategies.stream().allMatch(x -> ModelUtils.validatePreProcessingStrategy(x, lexerClass.getName(), lexerChannels))) {
			// strategy is not valid
			return null;
		}*/

		List<ModelDataItem> inputData = files.parallelStream().map(file -> {
			try {
				Lexer lexer = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromStream(file.getFileContents())); // build new lexer for each file
				List<? extends Token> tokensMaster = lexer.getAllTokens();

				ConcurrentMap<String, List<IndexedString>> map = new ConcurrentHashMap<>();

				preProcessingStrategies.parallelStream().forEach(strategy -> {  //now with 100% more parallel [maybe don't run this in parallel if we have lots of files?]
					if (strategy.isParserBased()) {
						for (Class<? extends IPreProcessor> processorClass : strategy.getPreProcessorClasses()) {
							try {
								IParserPreProcessor processor = (IParserPreProcessor) processorClass.newInstance();
								map.put(strategy.getName(), processor.processTokens(lexer, parserClass, workspace.getLanguage()));
							}
							catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					}
					else {
						List<? extends Token> tokens = new LinkedList<>(tokensMaster);
						for (Class<? extends IPreProcessor> processorClass : strategy.getPreProcessorClasses()) {
							try {
								ITokenPreProcessor processor = (ITokenPreProcessor) processorClass.newInstance();
								tokens = processor.process(tokens, lexer.getVocabulary(), workspace.getLanguage());
							}
							catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}

						ITokenStringifier stringifier;
						if (strategy.getStringifier() != null) {
							stringifier = strategy.getStringifier();
						}
						else if (strategy instanceof GenericTokenPreProcessingStrategy && ((GenericTokenPreProcessingStrategy) strategy).isResultTokenised()) {
							stringifier = new StandardTokeniser();
						}
						else {
							stringifier = new StandardStringifier();
						}

						map.put(strategy.getName(), stringifier.processTokens(tokens, lexer.getVocabulary()));
					}
				});

				return new ModelDataItem(file, map);
			}
			catch (InstantiationException | IllegalAccessException | IOException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}

			return null;
		}).collect(Collectors.toList());

		List<IDetector.IDetectorWorker> workers = instance.buildWorkers(inputData);
		workers.parallelStream().forEach(IDetector.IDetectorWorker::execute);

		List<AbstractModelTaskRawResult> raw = workers.stream().map(IDetector.IDetectorWorker::getRawResult).filter(x -> !x.isEmpty()).collect(Collectors.toList());
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
		SherlockRegistry.getPostProcessorInstance(raw.get(0).getClass());

		return raw.stream().map(Objects::toString).collect(Collectors.joining("\n----\n"));
	}

}
