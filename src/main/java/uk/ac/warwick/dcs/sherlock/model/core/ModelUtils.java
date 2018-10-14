package uk.ac.warwick.dcs.sherlock.model.core;

import com.google.common.collect.Streams;
import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelUtils {

	/**
	 * Checks a lexer conforms to the specification
	 *
	 * @param lexer         lexer instance to check
	 * @param specification specification to check against
	 *
	 * @return does it conform?
	 */
	public static boolean checkLexerAgainstSpecification(Lexer lexer, ILexerSpecification specification) {

		if (lexer.getChannelNames().length < specification.getChannelNames().length) {
			return false;
		}

		for (int i = 0; i < specification.getChannelNames().length; i++) {
			if (!lexer.getChannelNames()[i].equals(specification.getChannelNames()[i]) && !specification.getChannelNames().equals("-")) {
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

}
