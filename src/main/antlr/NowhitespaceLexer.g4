lexer grammar NowhitespaceLexer;

@header {
package uk.ac.warwick.dcs.sherlock.model.base.lang;
}

/*
 * White Space
 */
WS  :	[ ]
    ;
/*
 * Tabs
 */
TAB :	[\t]		-> skip
	;

/*
 * Consecutive Whitespace
 */
MWS	:	[ ]+		-> channel(HIDDEN)
	;

/*
 * New Lines
 */
NewLine : [\r\n]+ [ \t]*-> skip ;

/*
 *  Content 	- 	Everything else
 */
CONTENT :	.+?
	;