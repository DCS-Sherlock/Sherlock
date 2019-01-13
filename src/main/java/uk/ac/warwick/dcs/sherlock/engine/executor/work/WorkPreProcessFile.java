package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IParserPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IPreProcessingStrategy.GenericTokenPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.ITokenPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.ITokenStringifier;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardStringifier;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardTokeniser;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;

//TODO: switch to logger
public class WorkPreProcessFile extends RecursiveAction {

	private List<IWorkTask> tasks;
	private int begin;
	private int end;

	private ISourceFile file;
	private String fileContent;

	WorkPreProcessFile(List<IWorkTask> tasks, ISourceFile file) {
		this(tasks, 0, tasks.size(), file, file.getFileContentsAsString());
	}

	private WorkPreProcessFile(List<IWorkTask> tasks, int begin, int end, ISourceFile file, String fileContent) {
		this.tasks = tasks;
		this.begin = begin;
		this.end = end;

		this.file = file;
		this.fileContent = fileContent;
	}

	@Override
	protected void compute() {
		int size = this.end - this.begin;

		if (size > 1) {
			int middle = this.begin + (size / 2);
			WorkPreProcessFile t1 = new WorkPreProcessFile(this.tasks, this.begin, middle, this.file, this.fileContent);
			t1.fork();
			WorkPreProcessFile t2 = new WorkPreProcessFile(this.tasks, middle, this.end, this.file, this.fileContent);
			t2.compute();
			t1.join();
		}
		else {
			this.process(tasks.get(this.begin));
		}
	}

	@SuppressWarnings ("Duplicates")
	private void process(IWorkTask task) {
		try {
			Lexer lexer = task.getLexerClass().getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromString(this.fileContent));
			List<? extends Token> tokensMaster = lexer.getAllTokens();

			Map<String, List<IndexedString>> map = new HashMap<>();

			task.getPreProcessingStrategies().forEach(strategy -> {
				if (strategy.isParserBased()) {
					if (strategy.getPreProcessorClasses().size() == 1) { //this is checked by the registry on startup
						Class<? extends IPreProcessor> processorClass = strategy.getPreProcessorClasses().get(0);
						try {
							IParserPreProcessor processor = (IParserPreProcessor) processorClass.newInstance();
							map.put(strategy.getName(), processor.processTokens(lexer, task.getParserClass(), task.getLanguage()));
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
							tokens = processor.process(tokens, lexer.getVocabulary(), task.getLanguage());
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

			task.addModelDataItem(new ModelDataItem(this.file, map));
		}
		catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
