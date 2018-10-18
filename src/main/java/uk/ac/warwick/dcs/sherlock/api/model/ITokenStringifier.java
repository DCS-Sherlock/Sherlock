package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;

import java.util.*;

public interface ITokenStringifier {

	List<IndexedString> processTokens(List<? extends Token> tokens, Vocabulary vocab);

}
