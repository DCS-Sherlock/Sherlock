package uk.ac.warwick.dcs.sherlock.engine.model;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.model.*;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy.GenericTokenPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.data.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.data.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardStringifier;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardTokeniser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/* TODO: temporary implementation*/
public class TestResultsFactory {

	public static String buildTestResults(List<ISourceFile> files, Class<? extends IDetector> algorithm)
			throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

		IDetector instance = algorithm.newInstance();

		Class<? extends Lexer> lexerClass = instance.getLexer(Language.JAVA);
		Class<? extends Parser> parserClass = instance.getParser(Language.JAVA);

		List<IPreProcessingStrategy> preProcessingStrategies = instance.getPreProcessors();

		/*String[] lexerChannels = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromString("")).getChannelNames();
		if (!preProcessingStrategies.stream().allMatch(x -> ModelUtils.validatePreProcessingStrategy(x, lexerClass.getName(), lexerChannels))) {
			// strategy is not valid
			return null;
		}*/

		List<ModelDataItem> inputData = files.parallelStream().map(file -> {
			try {
				Lexer lexer = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromFileName(file.getFilename())); // build new lexer for each file
				List<? extends Token> tokensMaster = lexer.getAllTokens();

				ConcurrentMap<String, List<IndexedString>> map = new ConcurrentHashMap<>();

				preProcessingStrategies.parallelStream().forEach(strategy -> {  //now with 100% more parallel [maybe don't run this in parallel if we have lots of files?]
					if (strategy.isParserBased()) {
						for (Class<? extends IPreProcessor> processorClass : strategy.getPreProcessorClasses()) {
							try {
								IParserPreProcessor processor = (IParserPreProcessor) processorClass.newInstance();
								map.put(strategy.getName(), processor.processTokens(lexer, parserClass, Language.JAVA));
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
								tokens = processor.process(tokens, lexer.getVocabulary(), Language.JAVA);
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
		return workers.stream().map(IDetector.IDetectorWorker::getRawResult).map(Objects::toString).collect(Collectors.joining("\n----\n"));
	}

	public static class tmpFile implements ISourceFile {

		String filename;

		public tmpFile(String filename) {
			this.filename = filename;
		}

		@Override
		public InputStream getFileContents() {
			return null;
		}

		@Override
		public String getFilename() {
			return this.filename;
		}

		@Override
		public long getPersistentId() {
			return 0;
		}
	}

}
