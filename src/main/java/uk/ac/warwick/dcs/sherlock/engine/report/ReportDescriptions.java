package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.model.detection.LegecyDetectionType;

/**
 * A small class to supply the base descriptions for different kinds of plagiarism to the Report Generator.
 */
public class ReportDescriptions {

	/**
	 * Method to create a basic description that a ReportGenerator can use in a report.
	 * TODO: these descriptions aren't great and I think some of the detection types themselves can be removed/changed
	 *
	 * @param type The type of plagiarism that this description is needed for.
	 *
	 * @return A string with a basic description of the provided type of plagiarism, with placeholders for line numbers and so on.
	 */
	public static String getDescription(LegecyDetectionType type) {

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
				return "The expressions here are equivalent, but expressed in a different way.";
			case REDUNDANT_ADDED:
				return "The statements here are redundant (i.e. they serve no real purpose); the code is otherwise identical.";
			case STATEMENT_ORDER:
				return "The order of the statements here is different between the different files, but this does not affect the result of running the program.";
			case ITERATION_STRUCTURE:
				return "The loops here perform the same actions; they differ only in how they iterate over the loop variables, which is superficial in this case.";
			case SELECT_STRUCTURE:
				return "The structure of the select statements in each file has been altered slightly, which does not impact the result.";
			case BODY_REPLACE_CALL:
				return "The code in each program is identical, but where in one file a call to a function has been made, in another file the body of that function has been substituted in place of the call.";
			case NON_STRUCTURED_STATEMENTS:
				return "Statements have been introduced here in a non-structured manner, but the code is otherwise copied.";
			case COMBINE_NEW_OLD:
				return "This section of code contains a mixture of copied segments and original segments.";
		}

		return "";
	}

	/**
	 * Method to create the part of the description containing the file names and relevant line numbers.
	 *
	 * @param legecyDetectionType The type of plagiarism for this description. This is needed because some types need blocks of code while others need specific, individual lines.
	 * @param isContinued   If true, a semicolon and space is placed at the start of the string so the result can be appended to a previous location description.
	 *
	 * @return A string containing placeholders for the file names and line numbers, to be filled in by a ReportGenerator.
	 */
	public static String getLocationDescription(LegecyDetectionType legecyDetectionType, boolean isContinued) {
		String output = "";
		if (isContinued) {
			output = "; ";
		}

		if (legecyDetectionType == LegecyDetectionType.IDENTIFIER) {
			output += "File %1$s: first appearance at line %2$d";
		}
		else {
			output += "File %1$s: lines %2$d to %3$d";
		}

		return output;
	}

}