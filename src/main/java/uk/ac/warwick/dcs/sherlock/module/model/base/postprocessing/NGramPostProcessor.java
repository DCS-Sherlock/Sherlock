package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
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
		// TODO resolve to also accept similar line combos, as wells as identical ones
		// TODO find a better way to do this
		// for each possible pair check if they are the same file and the line numbers match up
		if (first.file1.equals(second.file1)) {
			return first.reference_lines.getKey() == second.reference_lines.getKey() && first.reference_lines.getValue() == second.reference_lines.getValue();
		} else if (first.file1.equals(second.file2)) {
			return first.reference_lines.getKey() == second.reference_lines.getKey() && first.check_lines.getValue() == second.check_lines.getValue();
		} else if (first.file2.equals(second.file1)) {
			return first.check_lines.getKey() == second.check_lines.getKey() && first.reference_lines.getValue() == second.reference_lines.getValue();
		} else if (first.file2.equals(second.file2)) {
			return first.check_lines.getKey() == second.check_lines.getKey() && first.check_lines.getValue() == second.check_lines.getValue();
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

	// TODO modularise this function as it's essentially the postproccessors main and shouldn't be doing much processing
	@Override
	public ModelTaskProcessedResults processResults(List<ISourceFile> files, List<NGramRawResult> rawResults) {

		ModelTaskProcessedResults results = new ModelTaskProcessedResults(new NGramScorer(threshold));

		ArrayList<ArrayList<NgramMatch>> matches = new ArrayList<>();

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
//					for (ArrayList<NgramMatch> check_list : matches) {
//						for (NgramMatch check : check_list) {
//							if (isLinked(match, check)) {
//								check_list.add(match);
//							}
//						}
//					}
				}
				// if nothing in matches add to match, else check isLinked and if true add to relvent list, if false add to new list
			}
		}
		// now all data is in the structure we can filter it

		/* can check here for if the matches are all connected for each list, if no then you can try and merge lists or cull from them to get
		a more informative result TODO: Implement this and attach it to a bool setting*/

		// once all processing is done make and score each results object
		for (ArrayList<NgramMatch> list : matches) {
			// make new scorer group
			((NGramScorer)results.getScorer()).newGroup();
			for (NgramMatch item : list) {
				// add all matches in a group to the scoring data structure
				((NGramScorer)results.getScorer()).add(item);
			}
			// make new group in results
			ICodeBlockGroup out_group = results.addGroup();
			// if group is bellow the threshold add all items to the group along with a score
			if (((NGramScorer)results.getScorer()).checkSize(files.size())) {
				for (ISourceFile file : ((NGramScorer)results.getScorer()).file_list) {
					((NGramScorer)results.getScorer()).getScore(file, out_group);
				}
			}
		}




		// for each item in raw results check for common code blocks, where the code block is not common in over threshold files add the code block to the
		// results list.

		// build a list of result lists, for each result check if any list exists that has common lines, check the 2 files connected by common file using N-Gram method
		// if it is above N-Grams threshold then add to said list (check for first or all in list? could make a setting. Also probably try to build a hierarchy based on similarity
		// to see if the source can be found(highest common match) then move highest to the front of the list)
		// once full list of lists is constructed the size of each list can be checked against the threshold. Anything below the threshold can be passed to the next stage

		// match each block in a list to the highest similarity (first in list) acquire score based on attributes (e.g. size, similarity, rate of plagiarism occurrence, etc)
		// once each block is processed add it to results

		// TODO IDEAS:
		// -The smaller the portion of files with a common block the higher the score, send the proportion to score along with other data

		// see docs, use:
		// x = results.addGroup();
		// x.addCodeBlock(..........); cont..

		return results;
	}
}
