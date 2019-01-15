package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import java.util.*;

public interface IWorkTask {

	void addModelDataItem(ModelDataItem item);

	Class<? extends IDetector> getDetector();

	Language getLanguage();

	Class<? extends Lexer> getLexerClass();

	Class<? extends Parser> getParserClass();

	List<IPreProcessingStrategy> getPreProcessingStrategies();

}
