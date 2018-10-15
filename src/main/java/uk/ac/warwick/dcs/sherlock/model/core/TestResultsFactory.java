package uk.ac.warwick.dcs.sherlock.model.core;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.*;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelDataItem;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.StandardStringifier;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.StandardTokeniser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/* TODO: temporary implementation*/
public class TestResultsFactory {

	public static void buildTest(List<ISourceFile> files, Class<? extends IDetector> algorithm)
			throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

		IDetector instance = algorithm.newInstance();

		Class<? extends Lexer> lexerClass = instance.getLexer(Language.JAVA);
		List<IPreProcessingStrategy> preProcessingStrategies = instance.getPreProcessors();
		//AtomicReference<Class<? extends Lexer>> lexerClass = new AtomicReference<>(instance.getLexer(Language.JAVA));
		//List<IPreProcessingStrategy> preProcessorClasses = Collections.synchronizedList(instance.getPreProcessors());

		String[] lexerChannels = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromString("")).getChannelNames();
		if (!preProcessingStrategies.stream().allMatch(x -> ModelUtils.validatePreProcessingStrategy(x, lexerClass.getName(), lexerChannels))) {
			// strategy is not valid
			return;
		}

		List<IModelDataItem> inputData = files.parallelStream().map(file -> {
			try {
				Lexer lexer = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromFileName(file.getFilename())); // build new lexer for each file
				ConcurrentMap<String, List<IndexedString>> map = new ConcurrentHashMap<>();

				// run each of the preprocessors and populate the file data with result
				//for (IPreProcessingStrategy strategy : preProcessingStrategies) {
				preProcessingStrategies.parallelStream().forEach(strategy -> {  //now with 100% more parallel
					List<? extends Token> tokens;
					synchronized (lexer) {
						lexer.reset();
						tokens = lexer.getAllTokens();
					}

					for (Class<? extends IPreProcessor> processorClass : strategy.getPreProcessorClasses()) {
						try {
							IPreProcessor processor = processorClass.newInstance();
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
					else if (strategy instanceof IPreProcessingStrategy.GenericPreProcessingStrategy && ((IPreProcessingStrategy.GenericPreProcessingStrategy) strategy).isResultTokenised()) {
						stringifier = new StandardTokeniser();
					}
					else {
						stringifier = new StandardStringifier();
					}

					final List<IndexedString> indexedStrings = stringifier.processTokens(tokens, lexer.getVocabulary());
					map.put(strategy.getName(), indexedStrings);
				});

				return new ModelDataItem(file, map);
			}
			catch (InstantiationException | IllegalAccessException | IOException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}

			return null;
		}).collect(Collectors.toList());

		List<IDetector.IDetectorWorker> workers = instance.buildWorkers(inputData, ModelResultItem.class);
		workers.parallelStream().forEach(IDetector.IDetectorWorker::run);
		workers.stream().map(IDetector.IDetectorWorker::getResult).forEach(x -> x.getAllPairedBlocks().forEach(System.out::println));
	}

	public static class tmpFile implements ISourceFile {

		String filename;

		public tmpFile(String filename) {
			this.filename = filename;
		}

		@Override
		public String getFilename() {
			return this.filename;
		}
	}

}
