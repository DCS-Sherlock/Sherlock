lexer grammar WhitespaceLexer;


@header {
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