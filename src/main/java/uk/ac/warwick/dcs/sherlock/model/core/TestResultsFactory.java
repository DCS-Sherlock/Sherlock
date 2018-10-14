package uk.ac.warwick.dcs.sherlock.model.core;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.data.internal.ModelDataItem;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/* TODO: temporary implementation*/
public class TestResultsFactory {

	public static void buildTest(List<ISourceFile> files, Class<? extends IDetector> algorithm) throws IllegalAccessException, InstantiationException {

		IDetector instance = algorithm.newInstance();
		Class<? extends Lexer> lexerClass = instance.getLexer(Language.JAVA);
		List<Class<? extends IPreProcessor>> preProcessorClasses = instance.getPreProcessors();

		List<IModelDataItem> inputData = files.parallelStream().map(file -> {
			try {
				Lexer lexer = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromFileName(file.getFilename())); // build new lexer for each file

				ModelDataItem data = new ModelDataItem(file); //data item for the file

				// run each of the preprocessors and populate the file data with result
				for (Class<? extends IPreProcessor> preProcessorClass : preProcessorClasses) {
					IPreProcessor preProcessor = preProcessorClass.newInstance();

					// check preprocessor valid
					if (ModelUtils.checkLexerAgainstSpecification(lexer, preProcessor.getLexerSpecification())) {
						lexer.reset();
						data.addPreProcessedLines(preProcessorClass, ModelUtils.convertSourceStream(preProcessor.process(lexer)));
					}
					else {
						System.out.println("Not a valid lexer for the preprocessor"); //better logging here please
					}

				}

				return data;
			}
			catch (InstantiationException | IllegalAccessException | IOException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}

			return null;
		}).collect(Collectors.toList());

		List<IDetector.IDetectorWorker> workers = instance.buildWorkers(inputData);
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
