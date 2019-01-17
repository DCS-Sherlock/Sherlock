package uk.ac.warwick.dcs.sherlock.engine.report;

/*
//Lines starting with // are ignored; so are blank lines
//Lines starting with :: denote a DetectionType enum

//Changing Comments or formatting
::COMMENT
Lines %1$d to %2$d in file A contain comments that are identical to lines %3$d to %4$d in file B.

//Changing Identifiers
::IDENTIFIER
The code in lines %1$d to %2$d in file A is identical to the code in lines %3$d to %4$d in file B, except that the identifiers have different names.

//Changing the order of operands in expressions
::OPERAND_ORDER
The expression at line %1$d to %2$d of file A is equivalent to the expression at line %3$d to %4$d of file B. The order that the operands are written in has been changed, but the result is the same.

//Changing Data Types
::DATA_TYPE
Variable X of file A is used in all the same ways as variable P of file B, but it is stored as a slightly different data type. This does not make a significant difference on the result however, and lines %1$d to %2$d of file A and lines %3$d to %4$d of file B are suspiciously similar.
//
*/

import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;

/**
 * A small class to supply the base descriptions for different kinds of plagiarism to the Report Generator.
 */
public class ReportDescriptions {

	/**
	 * Method to create a basic description that a ReportGenerator can use in a report.
	 * @param type The type of plagiarism that this description is needed for.
	 * @return A string with a basic description of the provided type of plagiarism, with placeholders for line numbers and so on.
	 */
	public static String getDescription(DetectionType type) {

		switch (type) {
			case COMMENT:
				return "Lines %1$d to %2$d in file A contain comments that are identical to lines %3$d to %4$d in file B";
			case IDENTIFIER:
				return "The code in lines %1$d to %2$d in file A is identical to the code in lines %3$d to %4$d in file B, except that the identifiers have different names";
			case OPERAND_ORDER:
				return "The expression at line %1$d to %2$d of file A is equivalent to the expression at line %3$d to %4$d of file B. The order that the operands are written in has been changed, but the result is the same.";
			case DATA_TYPE:
				return "Variable X of file A is used in all the same ways as variable P of file B, but it is stored as a slightly different data type. This does not make a significant difference on the result however, and lines %1$d to %2$d of file A and lines %3$d to %4$d of file B are suspiciously similar.";
			case EXPR_EQUIV:
				break;
			case REDUNDANT_ADDED:
				break;
			case STATEMENT_ORDER:
				break;
			case ITERATION_STRUCTURE:
				break;
			case SELECT_STRUCTURE:
				break;
			case BODY_REPLACE_CALL:
				break;
			case NON_STRUCTURED_STATEMENTS:
				break;
			case COMBINE_NEW_OLD:
				break;
		}

		return "";
	}

	/**
	 * Method to extend a description from getDescription an arbitrary length.
	 * @return a string containing a 'continuation' of a description from getDescription, to account for an arbitrary number of files in a ICodeBlockGroup.
	 */
	public static String getContinuedDescription() {
		//TODO: multiple types of extension needed (e.g. for variable names), and some of the base descriptions need editing to fit better with the continuation.
		//TODO cont: a better format may be e.g. File X lines 1-4, file Y lines 2-5, file Z lines 3-6...: [description] which can be copied for all descriptions basically.
		return ", lines %1$d to %2$d in file %3$d";
	}

}