package uk.ac.warwick.dcs.sherlock.model.preprocessing;

import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.stream.Stream;

public class TestPreProcessor implements IPreProcessor {

	@Override
	public Stream<String> process(Lexer lexer, Language lang) {
		ByteArrayInputStream s = new ByteArrayInputStream();
		BufferedInputStream b = new BufferedInputStream();

		return null;
	}
}
