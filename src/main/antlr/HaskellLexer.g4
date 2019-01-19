/*
 * File haskell-lexer.g
 * 
 * This file is an ANTLR grammar file that describes a lexer (scanner)
 * for Haskell.
 *
 * ANTLR is needed to translate this grammar to executable code. It is
 * freely available at http://www.antlr.org
 *
 * Author: Thiago Arrais - thiago.arrais@gmail.com
 */

 lexer grammar HaskellLexer;

@header {
package uk.ac.warwick.dcs.sherlock.module.model.base.lang;
}

channels { WHITESPACE, LONG_WHITESPACE, COMMENT  }

MODULE: 'module' ;
WHERE: 'where' ;
IMPORT: 'import' ;
QUALIFIED: 'qualified' ;
DERIVING: 'deriving' ;
AS: 'as' ;
HIDING: 'hiding' ;
TYPE: 'type' ;
DATA: 'data' ;
NEWTYPE: 'newtype' ;
CLASS: 'class' ;
INSTANCE: 'instance' ;
DEFAULT: 'default' ;
LET: 'let' ;
DO: 'do' ;
OF: 'of' ;
INFIXL: 'infixl' ;
INFIXR: 'infixr' ;
INFIX: 'infix' ;
CONTEXT_ARROW: '=>' ;
EQUALS: '=' ;
ALT: '|' ;
OFTYPE: '::' ;

// Whitespace and comments
WS  :  [ ] -> channel(WHITESPACE);
MWS : [ ]+ -> channel(LONG_WHITESPACE);
TAB : [\t]+ -> skip;
NEWLINE : [ \t]* [\r\n]+ [ \t]* -> skip;
BLOCK_COMMENT:            '{-' .*? '-}'    -> channel(COMMENT);
LINE_COMMENT:       '--' ~[\r\n]*    -> channel(COMMENT);

// Identifiers

IDENTIFIER:         Letter LetterOrDigit*;

// Fragment rules

fragment ExponentPart
    : [eE] [+-]? Digits
    ;

fragment HexDigits
    : HexDigit ((HexDigit | '_')* HexDigit)?
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

fragment Digits
    : [0-9] ([0-9_]* [0-9])?
    ;

fragment LetterOrDigit
    : Letter
    | [0-9]
    ;

fragment Letter
    : [a-zA-Z$_] // these are the 'java letters' below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;