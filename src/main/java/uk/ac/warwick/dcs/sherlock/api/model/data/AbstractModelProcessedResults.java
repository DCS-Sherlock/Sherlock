package uk.ac.warwick.dcs.sherlock.api.model.data;

import uk.ac.warwick.dcs.sherlock.api.model.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.io.Serializable;
import java.util.*;

// Make interface and make a creator interface in the API
public abstract class AbstractModelProcessedResults implements Serializable {

	private static final long serialVersionUID = 34L;

	/*private List<ResultsInstance> data;

	public ModelProcessedResults() {
		this.data = new LinkedList<>();
	}*/

	// TODO methods

	class ResultsInstance {

		private ISourceFile file;
		private DetectionType type;
		private float overallScore;

		private List<ResultsBlock> blockMap;
		private Map<ISourceFile, ResultsFile> fileMap;
	}

	class ResultsFile {

		private float fileScore;
		private List<ResultsBlock> blocksContaingFile;
	}

	class ResultsBlock {

		Tuple<Integer, Integer> parentBlock;
		float blockScore;
		Map<ISourceFile, Tuple<Integer, Integer>> associatedBlocks;
	}

}
