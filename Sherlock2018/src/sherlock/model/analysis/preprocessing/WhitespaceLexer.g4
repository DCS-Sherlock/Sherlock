lexer grammar WhitespaceLexer;

@header {
	package sherlock.model.analysis.preprocessing;
}

/*-------------------------
 *  Whitespace
 *-------------------------*/

WS  :	[ ]
    ;

TAB :	[\t]
	;

NEWLINE : [\r\n]+ -> skip
	;

/*-------------------------
 *  Content
 *-------------------------*/
CONTENT :	.+? -> skip
	;