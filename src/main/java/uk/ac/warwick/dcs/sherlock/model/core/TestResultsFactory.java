package uk.ac.warwick.dcs.sherlock.model.core;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IModelResultItem;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.model.base.data.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.model.base.data.ModelResultItem;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/* TODO: temporary implementation*/
public class TestResultsFactory {

	public static IModelResultItem build(List<ISourceFile> files, Class<? extends IDetector> algorithm) throws IllegalAccessException, InstantiationException, IOException {

		IDetector instance = algorithm.newInstance();
		Lexer lexer = instance.getLexer(Language.JAVA).newInstance();

		ModelDataItem data = new ModelDataItem(files);

		// This is disgusting i know :/ only a temporary testing implementation
		for (Iterator<Class<? extends IPreProcessor>> it = instance.getPreProcessors().iterator(); it.hasNext(); ) {
			IPreProcessor p = it.next().newInstance();

			if (!ModelUtils.checkLexerAgainstSpecification(lexer, p.getLexerSpecification().newInstance())) {
				System.out.println("Not a valid lexer for the preprocessor"); //better logging here please
				break;
			}

			//Process each file and add to the dataset, would also reference the preprocessor in a real implementation
			for (ISourceFile file : files) {
				lexer.setInputStream(CharStreams.fromFileName(file.getFilename()));
				data.addPreProcessedFileData(null, file, p.process(lexer, Language.JAVA));
			}
		}

		ModelResultItem result = new ModelResultItem(files, algorithm);

		instance.execute(data, result);

		return result;
	}

}
