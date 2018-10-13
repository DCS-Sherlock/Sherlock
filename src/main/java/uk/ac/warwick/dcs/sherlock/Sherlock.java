package uk.ac.warwick.dcs.sherlock;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.lib.Reference;
import uk.ac.warwick.dcs.sherlock.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.processors.SourceTokeniser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Sherlock {

	public static void main(String[] args) {
		if (Reference.isDevelEnv) System.out.println("Sherlock vX.X.X [Development Version]\n");
		else  System.out.println(String.format("Sherlock v%s\n", Reference.version));

		try {
			Lexer lexer = new JavaLexer(CharStreams.fromFileName("test.java"));
			IPreProcessor tmp = new SourceTokeniser();
			Stream<String> s = tmp.process(lexer, Language.JAVA);

			AtomicInteger atomicInteger = new AtomicInteger(0);
			s.forEach(name -> {
						atomicInteger.getAndIncrement();
						System.out.println(atomicInteger + ": " + name);
					});
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
