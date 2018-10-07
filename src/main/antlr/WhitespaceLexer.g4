lexer grammar WhitespaceLexer;

@header {
package uk.ac.warwick.dcs.sherlock.services.preprocessing;
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