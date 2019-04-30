package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.*;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy.GenericGeneralPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorUtils;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardStringifier;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.StandardTokeniser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Recursive task to preprocess a list of files for a single task
 */
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
		try {
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
				if (this.tasks != null && !this.tasks.isEmpty()) {
					this.process(tasks.get(this.begin));
				}
				else {
					synchronized (ExecutorUtils.logger) {
						ExecutorUtils.logger.error("Strategy could not be preprocessed, no work tasks exist");
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings ("Duplicates")
	private void process(IWorkTask task) {
		Map<String, List<IndexedString>> map = new HashMap<>();

		task.getPreProcessingStrategies().forEach(strategy -> {
			if (strategy.isAdvanced()) {
				try {
					Class<? extends IAdvancedPreProcessorGroup> groupClass = (Class<? extends IAdvancedPreProcessorGroup>) strategy.getPreProcessorClasses().get(0);
					ITuple<Class<? extends IAdvancedPreProcessor>, Class<? extends Lexer>> t = SherlockRegistry.getAdvancedPostProcessorForLanguage(groupClass, task.getLanguage());

					Lexer lexer = t.getValue().getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromStream(file.getFileContents()));
					IAdvancedPreProcessor processor = t.getKey().getConstructor().newInstance();
					map.put(strategy.getName(), processor.process(lexer));
				}
				catch (InstantiationException | IllegalAccessException | NoSuchMethodException | IOException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					Class<? extends Lexer> clazz = SherlockRegistry.getLexerForStrategy(strategy, task.getLanguage());
					if (clazz != null) {
						Lexer lexer = clazz.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromString(this.fileContent));
						List<? extends Token> tokensMaster = lexer.getAllTokens();

						List<? extends Token> tokens = new LinkedList<>(tokensMaster);
						for (Class<? extends IPreProcessor> processorClass : strategy.getPreProcessorClasses()) {
							try {
								IGeneralPreProcessor processor = (IGeneralPreProcessor) processorClass.getConstructor().newInstance();
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
						else if (strategy instanceof GenericGeneralPreProcessingStrategy && ((GenericGeneralPreProcessingStrategy) strategy).isResultTokenised()) {
							stringifier = new StandardTokeniser();
						}
						else {
							stringifier = new StandardStringifier();
						}

						map.put(strategy.getName(), stringifier.processTokens(tokens, lexer.getVocabulary()));
					}
					else {
						synchronized (ExecutorUtils.logger) {
							ExecutorUtils.logger.error("Strategy is not valid for the passed language, this should have been caught at startup!");
						}
					}
				}
				catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
					e.printStackTrace();
				}
			}
			task.getJobStatus().incrementProgress();
		});

		task.addModelDataItem(new ModelDataItem(this.file, map));
	}
}
