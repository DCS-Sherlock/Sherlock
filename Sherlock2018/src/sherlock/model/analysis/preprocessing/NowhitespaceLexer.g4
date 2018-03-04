lexer grammar NowhitespaceLexer;

@header {
	package sherlock.model.analysis.preprocessing;
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