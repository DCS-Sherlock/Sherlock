package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;

import java.util.*;

public interface ITokenStringifier {

	List<IndexedString> processTokens(List<? extends Token> tokens, Vocabulary vocab);

}
