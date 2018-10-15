package uk.ac.warwick.dcs.sherlock.model.core;

import com.google.common.collect.Streams;
import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	 * @param strategy {@link IPreProcessingStrategy} instance to check
	 * @param lexer {@link Lexer} instance to check
	 * @return is the strategy valid
	 */
	public static boolean validatePreProcessingStrategy(IPreProcessingStrategy strategy, Lexer lexer) {
		return validatePreProcessingStrategy(strategy, lexer.getChannelNames());
	}

	/**
	 * Check that an instance of {@link IPreProcessingStrategy} is valid, are all preprocessor dependencies met and do they all support the lexer
	 * @param strategy {@link IPreProcessingStrategy} instance to check
	 * @param lexerChannels array of channels used in {@link Lexer}, to check
	 * @return is the strategy valid
	 */
	public static boolean validatePreProcessingStrategy(IPreProcessingStrategy strategy, String[] lexerChannels) {
		return strategy == null;
	}

}
