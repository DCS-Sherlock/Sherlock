package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NgramMatch;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.ModelTaskProcessedResults;
import uk.ac.warwick.dcs.sherlock.module.model.base.scoring.NGramScorer;

import java.util.*;

public class NGramPostProcessor implements IPostProcessor<NGramRawResult> {

	/**
	 * Threshold determining when to ignore large sets of matches.
	 * <p>
	 *     If a block of code is common among a large set of files it is less likely to be plagiarism and
	 *     more likely a common code pattern or something given to the students (e.g. skeleton files).
	 *     This threshold determines the percentage of files over which matches will be ignored, to avoid
	 *     false detections.
	 * </p>
	 */
	@AdjustableParameter (name = "Common Threshold", defaultValue = 0.3f, minimumBound = 0.0f, maxumumBound = 1.0f, step = 0.001f)
	public float threshold;

	/**
	 * A method to check if 2 match objects are for the same code block.
	 * @param first The first match to compare.
	 * @param second The second match to compare.
	 * @return A boolean showing if the 2 matches are on the same code block or not.
	 */
	private boolean isLinked(NgramMatch first, NgramMatch second) {
		// TODO find a better way to do this
		// precompute key and value matches. This is slightly more inefficient than doing it post file match check, but improves readability for negligible loss
		// if the start lines of 2 blocks match up
		boolean r_key = first.reference_lines.getKey() == second.reference_lines.getKey();
		boolean c_key = first.check_lines.getKey() == second.check_lines.getKey();
		// if the end lines of the 2 blocks line up
		boolean r_val = first.reference_lines.getValue() == second.reference_lines.getValue();
		boolean c_val = first.check_lines.getValue() == second.check_lines.getValue();
		// for each possible pair check if they are the same file and the line numbers match up
		if (first.file1.equals(second.file1)) {
			return r_key && r_val;
		} else if (first.file1.equals(second.file2)) {
			return r_key && c_val;
		} else if (first.file2.equals(second.file1)) {
			return c_key && r_val;
		} else if (first.file2.equals(second.file2)) {
			return c_key && c_val;
		}
		// if no match is found
		return false;
	}

	/**
	 * Add a matched pair to the matched groups data structure.
	 * @param match The match to be added to the structure.
	 * @param matches The structure to add the match to.
	 */
	private void addToMatches(NgramMatch match, ArrayList<ArrayList<NgramMatch>> matches) {
		// for each list check for a link in the list by checking all contained objects. Add to any list wish a link or make a new list
		for (ArrayList<NgramMatch> check_list : matches) {
			for (NgramMatch check : check_list) {
				// if link found add to list
				if (isLinked(match, check)) {
					check_list.add(match);
					return;
				}
			}
		}
		// if no link is found make new list to add match to it
		matches.add(new ArrayList<>());
		matches.get(matches.size()-1).add(match);
	}

	private void processMatches(List<NGramRawResult> rawResults, ArrayList<ArrayList<NgramMatch>> matches) {
		// I'd like to apologise for this abomination of code
		// checks for any link between already seen matches and new ones, then adds them to the structure accordingly
		for (NGramRawResult result : rawResults ) {			// for each file
			for (NgramMatch match : (List<NgramMatch>) result.objects) {  //for each match in said file
				// if first match being checked add to new sublist
				if (matches.size() == 0) {
					matches.add(new ArrayList<>());
					matches.get(0).add(match);
				} else {
					// for each list check for a link in the list by checking all contained objects. Add to any link or make a new list
					addToMatches(match, matches);
				}
				// if nothing in matches add to match, else check isLinked and if true add to relevant list, if false add to new list
			}
		}
	}

	private void makeScoreGroups(List<ISourceFile> files, ModelTaskProcessedResults results, ArrayList<ArrayList<NgramMatch>> matches, ICodeBlockGroup out_group, NGramScorer scorer) {
		// for each matching group of code blocks
		for (ArrayList<NgramMatch> list : matches) {
			// make new scorer group
			scorer.newGroup();
			// fill the scorer group
			for (NgramMatch item : list) {
				// add all matches in a group to the scoring data structure
				scorer.add(item);
			}
			// if the last group was used, make a new group in results
			if (out_group.getCodeBlocks().size() != 0) {
				out_group = results.addGroup();
			}
			// if group is bellow the threshold add all items to the group along with a score
			if (scorer.checkSize(files.size(), list)) {
				// get the score for each file in the list
				for (ISourceFile file : scorer.file_list) {
					scorer.getScore(file, out_group);
				}
				out_group.setComment("N-Gram Match Group");
				try {
					out_group.setDetectionType("BASE_BODY_REPLACE_CALL");
				}
				catch (UnknownDetectionTypeException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public ModelTaskProcessedResults processResults(List<ISourceFile> files, List<NGramRawResult> rawResults) {

		ModelTaskProcessedResults results = new ModelTaskProcessedResults();
		NGramScorer scorer = new NGramScorer(threshold);

		// A list of all match block groups
		// Each group is stored as a sub list and has the same common code block
		ArrayList<ArrayList<NgramMatch>> matches = new ArrayList<>();

		// checks for any link between already seen matches and new ones, then adds them to the structure accordingly
		processMatches(rawResults, matches);
		// now all data is in the structure we can filter it

		/* can check here for if the matches are all connected for each list, if no then you can try and merge lists or cull from them to get
		a more informative result TODO: Implement this and attach it to a bool setting*/

		// make new group in results
		ICodeBlockGroup out_group = results.addGroup();

		// once all processing is done make and score each results object
		makeScoreGroups(files, results, matches, out_group, scorer);

		// if last group is common removed then remove the empty group from results
		if (out_group.getCodeBlocks().size() == 0) {
			results.removeGroup(out_group);
		}

//		System.out.println("processResults");
		return results;
	}
}