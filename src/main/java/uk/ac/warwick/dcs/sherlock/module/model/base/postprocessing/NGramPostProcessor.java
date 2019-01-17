package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NgramMatch;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.ModelTaskProcessedResults;
import uk.ac.warwick.dcs.sherlock.module.model.base.scoring.NGramScorer;

import java.util.*;

public class NGramPostProcessor implements IPostProcessor<NGramRawResult> {

	@AdjustableParameter (name = "Test Param", defaultValue = 0, minimumBound = 0, maxumumBound = 10, step = 1)
	public int testParam;

	// method to check for a link between pairs
	public boolean isLinked(NgramMatch first, NgramMatch second, ISourceFile file1, ISourceFile file2, ISourceFile file3, ISourceFile file4) {
		// TODO resolve to also accept similar line combos, as wells as identical ones
		// TODO check this comparison works / find a better way to do this
		if (file1 == file3) {
			return first.reference_lines.getKey() == second.reference_lines.getKey() && first.reference_lines.getValue() == second.reference_lines.getValue();
		} else if (file1 == file4) {
			return first.reference_lines.getKey() == second.reference_lines.getKey() && first.check_lines.getValue() == second.check_lines.getValue();
		} else if (file2 == file3) {
			return first.check_lines.getKey() == second.check_lines.getKey() && first.reference_lines.getValue() == second.reference_lines.getValue();
		} else if (file2 == file4) {
			return first.check_lines.getKey() == second.check_lines.getKey() && first.check_lines.getValue() == second.check_lines.getValue();
		}
		return false;
	}

	@Override
	public ModelTaskProcessedResults processResults(List<ISourceFile> files, List<NGramRawResult> rawResults) {
		// TODO pass data to scorer once scoring method has been figured out
		ModelTaskProcessedResults results = new ModelTaskProcessedResults(new NGramScorer());

		ArrayList<ArrayList<NgramMatch>> matches = new ArrayList<>();

		for (NGramRawResult result : rawResults )
			for (NgramMatch match : (List<NgramMatch>) result.objects) {
				// if nothing in matches add to match, else check isLinked and if true add to relvent list, if false add to new list
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
