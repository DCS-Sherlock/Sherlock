parser grammar WhitespaceParser;

options {
	tokenVocab = WhitespaceLexer;
}

@header {
}

start :	file*;

file :	WS
	|	CONTENT
	|	TAB
	;