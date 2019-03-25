package uk.ac.warwick.dcs.sherlock.api.model.scoring;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

@Deprecated
public interface IScoreFunction {

	/**
	 * Returns a score based on how much the main file plagiarises the reference file
	 *
	 * @param mainFile      the subject of the scoring function.
	 * @param referenceFile the reference to score the subject against
	 * @param mutualGroups  the ICodeBlockGroups where both the main and subject files are present
	 *
	 * @return a score as a float in range 0 to 1
	 */
	float score(ISourceFile mainFile, ISourceFile referenceFile, List<ICodeBlockGroup> mutualGroups);

}
