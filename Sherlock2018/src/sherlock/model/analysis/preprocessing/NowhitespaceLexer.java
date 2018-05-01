// Generated from NowhitespaceLexer.g4 by ANTLR 4.7.1

	package sherlock.model.analysis.preprocessing;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NowhitespaceLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, TAB=2, MWS=3, NewLine=4, CONTENT=5;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"WS", "TAB", "MWS", "NewLine", "CONTENT"
	};

	private static final String[] _LITERAL_NAMES = {
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "WS", "TAB", "MWS", "NewLine", "CONTENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public NowhitespaceLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "NowhitespaceLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\7,\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\3\3\3\3\3\3\3\3\4\6\4\25\n\4"+
		"\r\4\16\4\26\3\4\3\4\3\5\6\5\34\n\5\r\5\16\5\35\3\5\7\5!\n\5\f\5\16\5"+
		"$\13\5\3\5\3\5\3\6\6\6)\n\6\r\6\16\6*\3*\2\7\3\3\5\4\7\5\t\6\13\7\3\2"+
		"\6\3\2\"\"\3\2\13\13\4\2\f\f\17\17\4\2\13\13\"\"\2/\2\3\3\2\2\2\2\5\3"+
		"\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\3\r\3\2\2\2\5\17\3\2\2\2\7"+
		"\24\3\2\2\2\t\33\3\2\2\2\13(\3\2\2\2\r\16\t\2\2\2\16\4\3\2\2\2\17\20\t"+
		"\3\2\2\20\21\3\2\2\2\21\22\b\3\2\2\22\6\3\2\2\2\23\25\t\2\2\2\24\23\3"+
		"\2\2\2\25\26\3\2\2\2\26\24\3\2\2\2\26\27\3\2\2\2\27\30\3\2\2\2\30\31\b"+
		"\4\3\2\31\b\3\2\2\2\32\34\t\4\2\2\33\32\3\2\2\2\34\35\3\2\2\2\35\33\3"+
		"\2\2\2\35\36\3\2\2\2\36\"\3\2\2\2\37!\t\5\2\2 \37\3\2\2\2!$\3\2\2\2\""+
		" \3\2\2\2\"#\3\2\2\2#%\3\2\2\2$\"\3\2\2\2%&\b\5\2\2&\n\3\2\2\2\')\13\2"+
		"\2\2(\'\3\2\2\2)*\3\2\2\2*+\3\2\2\2*(\3\2\2\2+\f\3\2\2\2\7\2\26\35\"*"+
		"\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}