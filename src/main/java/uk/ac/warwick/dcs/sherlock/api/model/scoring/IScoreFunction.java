package uk.ac.warwick.dcs.sherlock.api.model.scoring;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

public interface IScoreFunction {

	/**
	 * Returns a score based on how much the main file plagiarises the reference file
	 *
	 * @param mainFile
	 * @param referenceFile
	 *
	 * @return
	 */
	float score(ISourceFile mainFile, ISourceFile referenceFile, List<ICodeBlockGroup> mutualGroups);

}
