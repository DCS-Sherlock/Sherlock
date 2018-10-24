package uk.ac.warwick.dcs.sherlock.engine.model;

import com.google.common.collect.Streams;
import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;

import java.util.*;
import java.util.stream.*;

public class ModelUtils {

	/**
	 * Checks a lexer conforms to the specification
	 *
	 * @param lexer         {@link Lexer} instance to check
	 * @param specification {@link ILexerSpecification} to check against
	 *
	 * @return does it conform?
	 */
	public static boolean checkLexerAgainstSpecification(Lexer lexer, ILexerSpecification specification) {
		return checkLexerAgainstSpecification(lexer.getChannelNames(), specification);
	}

	/**
	 * Checks a lexer conforms to the specification
	 *
	 * @param lexerChannels array of channels used in {@link Lexer}, to check
	 * @param specification {@link ILexerSpecification} to check against
	 *
	 * @return does it conform?
	 */
	public static boolean checkLexerAgainstSpecification(String[] lexerChannels, ILexerSpecification specification) {

		if (lexerChannels.length < specification.getChannelNames().length) {
			return false;
		}

		for (int i = 0; i < specification.getChannelNames().length; i++) {
			if (!lexerChannels[i].equals(specification.getChannelNames()[i]) && !specification.getChannelNames().equals("-")) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Converts a source stream of file lines into an indexed set of non blank lines, retains original line numbers in index
	 *
	 * @param stream input stream, from raw file or from preprocessors
	 *
	 * @return list of indexed lines
	 */
	public static List<IndexedString> convertSourceStream(Stream<String> stream) {
		return Streams.mapWithIndex(stream, (str, index) -> IndexedString.of((int) index + 1, str)).filter(s -> !s.getValue().isEmpty()).collect(Collectors.toList());
	}

	/**
	 * Check that an instance of {@link IPreProcessingStrategy} is valid, are all preprocessor dependencies met and do they all support the lexer
	 *
	 * @param strategy {@link IPreProcessingStrategy} instance to check
	 * @param lexer    {@link Lexer} instance to check
	 *
	 * @return is the strategy valid
	 */
	public static boolean validatePreProcessingStrategy(IPreProcessingStrategy strategy, Lexer lexer) {
		return validatePreProcessingStrategy(strategy, lexer.getClass().getName(), lexer.getChannelNames());
	}

	/**
	 * Check that an instance of {@link IPreProcessingStrategy} is valid, are all preprocessor dependencies met and do they all support the lexer
	 *
	 * @param strategy      {@link IPreProcessingStrategy} instance to check
	 * @param lexerChannels array of channels used in {@link Lexer}, to check
	 *
	 * @return is the strategy valid
	 */
	public static boolean validatePreProcessingStrategy(IPreProcessingStrategy strategy, String lexerName, String[] lexerChannels) {

		if (strategy == null) {
			return false;
		}

		List<Class<? extends IPreProcessor>> checkedProcessors = new LinkedList<>();
		for (Class<? extends IPreProcessor> processorClass : strategy.getPreProcessorClasses()) {
			try {
				IPreProcessor processor = processorClass.newInstance();
				if (!checkLexerAgainstSpecification(lexerChannels, processor.getLexerSpecification())) {
					Logger logger = LoggerFactory.getLogger(ModelUtils.class);
					logger.error(String.format("%s does not conform to the required lexer specification for %s", lexerName, processorClass.getName())); //throw exception here
					return false;
				}

				if (processor.getDependencies() != null && !checkedProcessors.containsAll(processor.getDependencies())) {
					Logger logger = LoggerFactory.getLogger(ModelUtils.class);
					logger.error(String.format("The preprocessing strategy '%s' does not meet the dependencies of %s", strategy.getName(), processorClass.getName())); //throw exception here
					return false;
				}
				checkedProcessors.add(processorClass);
			}
			catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

}
