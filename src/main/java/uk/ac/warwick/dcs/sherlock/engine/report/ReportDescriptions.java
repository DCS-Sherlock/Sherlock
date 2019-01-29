package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;

/**
 * A small class to supply the base descriptions for different kinds of plagiarism to the Report Generator.
 */
public class ReportDescriptions {

	/**
	 * Method to create a basic description that a ReportGenerator can use in a report.
	 *
	 * @param type The type of plagiarism that this description is needed for.
	 *
	 * @return A string with a basic description of the provided type of plagiarism, with placeholders for line numbers and so on.
	 */
	public static String getDescription(DetectionType type) {

		switch (type) {
			case COMMENT:
				return "The specified comments are identical to each other.";
			case IDENTIFIER:
				return "These blocks of code are identical in every respect except that different names are used for their identifiers.";
			case OPERAND_ORDER:
				return "The expressions at these locations are identical, except that the operands are listed in different orders. The statements evaluate to the same result.";
			case DATA_TYPE:
				return "These blocks of code are the same except for the data type that the variables are stored in. This does not make a significant difference on the result.";
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
	 * Method to create the part of the description containing the file names and relevant line numbers.
	 *
	 * @param detectionType The type of plagiarism for this description. This is needed because some types need blocks of code while others need specific, individual lines.
	 * @param isContinued If true, a semicolon and space is placed at the start of the string so the result can be appended to a previous location description.
	 *
	 * @return A string containing placeholders for the file names and line numbers, to be filled in by a ReportGenerator.
	 */
	public static String getLocationDescription(DetectionType detectionType, boolean isContinued) {
		String output = "";
		if (isContinued) {
			output = "; ";
		}

		if(detectionType == DetectionType.IDENTIFIER) {
			output += "File %1$s: first appearance at line %2$d";
		} else {
			output += "File %1$s: lines %2$d to %3$d";
		}

		return output;
	}

}