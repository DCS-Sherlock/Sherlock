package uk.ac.warwick.dcs.sherlock.api.model.detection;

import java.util.*;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

/**
 * A list of different kinds of plagiarism that may be detected. Currently based on "Identification of Program Similarity in Large populations" by Whale (Aliyah's report).
 * <p>
 * (Not sure how usable all of these are in practice)
 */
public enum DetectionType {COMMENT, IDENTIFIER, OPERAND_ORDER, DATA_TYPE, EXPR_EQUIV, REDUNDANT_ADDED, STATEMENT_ORDER, ITERATION_STRUCTURE, SELECT_STRUCTURE, BODY_REPLACE_CALL,
	NON_STRUCTURED_STATEMENTS, COMBINE_NEW_OLD}

/* WIP!!!!!!!
this is to replace the current version of DetectionType and let users add their own reasons somehow.
I won't make it final until everything is in place

Now instead of storing a DetectionType enum, store a string which is the name of the reason?
alternative: DetectionType is a single reason, and theres another class to store all of them?
*/
/*
public class DetectionType {
	/**
	 * A map containing each possible justification. The key is a string for the name of that reasons.
	 * The value is a tuple containing a description of that reason, and a float (0 to 1) with the weighting for it.
	 *
	private static Map<String, ITuple<String, Float>> reasons;

	public DetectionType(List<String> extraReasonNames, List<String> extraReasonDescriptions, List<Float> extraReasonWeights) {
		reasons = new HashMap<>();

		//add default reasons
		reasons.put("COMMENT", new Tuple<>("The specified comments are identical to each other.", 1f));
		reasons.put("VARIABLE_NAME", new Tuple<>("This variable has been used in the same way in each file, but has a different name.", 1f));
		reasons.put("METHOD_NAME", new Tuple<>("The methods in each file are identical in content but have different names.", 1f));
		reasons.put("COPIED_BLOCK", new Tuple<>("The blocks in these files are completely identical to each other.", 1f));
		//etc...

		//add user submitted reasons
		for(int i = 0; i < extraReasonNames.size(); i++) {
			reasons.put(extraReasonNames.get(i), new Tuple<>(extraReasonDescriptions.get(i), extraReasonWeights.get(i)));
		}
	}

	/**
	 * @return the full map of reasons, including every kind of plagiarism type, including name, description and score weighting.
	 *
	public static Map<String, ITuple<String, Float>> getReasons() {
		return reasons;
	}

	 /**
	 * @return a set of every detection type name.
	 *
	public static Set<String> getReasonNames() {
		return reasons.keySet();
	}

	/**
	 * Given a type of plagiarism, returns the information relevant to it.
	 *
	 * @param name The name of the type of plagiarism to get information for.
	 * @return The tuple containing the description and score weighting of the specified plagiarism type.
	 *
	public static ITuple<String, Float> getReasonInfo(String name) {
		return reasons.get(name);
	}
}
*/