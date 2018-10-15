package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;

import java.util.List;

public interface ITokeniser {

	List<IndexedString> processTokens(List<Token> tokens, Vocabulary vocab);

}
