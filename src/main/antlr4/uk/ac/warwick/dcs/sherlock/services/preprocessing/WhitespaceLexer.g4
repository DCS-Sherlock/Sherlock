lexer grammar WhitespaceLexer;


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