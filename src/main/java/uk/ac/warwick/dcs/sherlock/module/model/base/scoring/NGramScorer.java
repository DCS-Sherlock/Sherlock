package uk.ac.warwick.dcs.sherlock.module.model.base.scoring;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.scoring.IScoreFunction;

import java.util.*;

/**
 * TODO: WRITE THIS
 *
 * Store the num objects (from rawresult object) for each of the files,
 * compare the number of non-overlapping groups file is present in with the total number of objects to get a score for the file
 */
public class NGramScorer implements IScoreFunction {

	@Override
	public float score(ISourceFile mainFile, ISourceFile referenceFile, List<ICodeBlockGroup> mutualGroups) {
		return 0;
	}
}
