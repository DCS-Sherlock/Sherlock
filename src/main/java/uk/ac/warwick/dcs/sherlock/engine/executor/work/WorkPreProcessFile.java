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

	private IWorkTask task;
	private ISourceFile file;
	private String fileContent;

	WorkPreProcessFile(IWorkTask task, ISourceFile file, String fileContent) {
		this.task = task;
		this.file = file;
		this.fileContent = fileContent;
	}

	@SuppressWarnings ("Duplicates")
	@Override
	protected void compute() {
		try {
			Lexer lexer = this.task.getLexerClass().getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromString(this.fileContent));
			List<? extends Token> tokensMaster = lexer.getAllTokens();

			Map<String, List<IndexedString>> map = new HashMap<>();

			this.task.getPreProcessingStrategies().forEach(strategy -> {
				if (strategy.isParserBased()) {
					if (strategy.getPreProcessorClasses().size() == 1) { //this is checked by the registry on startup
						Class<? extends IPreProcessor> processorClass = strategy.getPreProcessorClasses().get(0);
						try {
							IParserPreProcessor processor = (IParserPreProcessor) processorClass.newInstance();
							map.put(strategy.getName(), processor.processTokens(lexer, this.task.getParserClass(), this.task.getLanguage()));
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
							tokens = processor.process(tokens, lexer.getVocabulary(), this.task.getLanguage());
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

			this.task.addModelDataItem(new ModelDataItem(this.file, map));
		}
		catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
