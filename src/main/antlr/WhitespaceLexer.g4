lexer grammar WhitespaceLexer;

@header {
package uk.ac.warwick.dcs.sherlock.model.base.lang;
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