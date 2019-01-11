package uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IParserPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.api.common.IndexedString;
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaParser;
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaParserBaseListener;

import java.util.*;

public class VariableExtractor implements IParserPreProcessor {

	@Override
	public Class<? extends Parser> getParserUsed(Language lang) {
		return JavaParser.class;
	}

	@Override
	public List<IndexedString> processTokens(Lexer lexer, Class<? extends Parser> parserClass, Language lang) {
		List<IndexedString> fields = new LinkedList<>();

		if (lang == Language.JAVA) {
			lexer.reset();
			JavaParser parser = new JavaParser(new CommonTokenStream(lexer));

			ParseTreeWalker.DEFAULT.walk(new JavaParserBaseListener() {
				//globals
				/*@Override
				public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
					fields.add(new IndexedString(ctx.start.getLine(), ctx.getText()));
				}*/

				//locals
				@Override
				public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
					fields.add(new IndexedString(ctx.start.getLine(), ctx.getText().split("=")[0]));
				}
			}, parser.compilationUnit());

			//System.out.println("field -> " + fields.toString());
		}
		return fields;
	}
}
