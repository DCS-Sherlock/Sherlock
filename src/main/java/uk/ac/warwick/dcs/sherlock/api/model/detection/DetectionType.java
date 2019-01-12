package uk.ac.warwick.dcs.sherlock.api.model.detection;

/**
 * A list of different kinds of plagiarism that may be detected. Currently based on "Identification of Program Similarity in Large populations" by Whale (Aliyah's report).
 * <p>
 * (Not sure how usable all of these are in practice)
 */
public enum DetectionType {COMMENT, IDENTIFIER, OPERAND_ORDER, DATA_TYPE, EXPR_EQUIV, REDUNDANT_ADDED, STATEMENT_ORDER, ITERATION_STRUCTURE, SELECT_STRUCTURE, BODY_REPLACE_CALL,
	NON_STRUCTURED_STATEMENTS, COMBINE_NEW_OLD}
