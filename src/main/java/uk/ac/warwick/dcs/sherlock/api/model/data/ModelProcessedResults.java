package uk.ac.warwick.dcs.sherlock.api.model.data;

import uk.ac.warwick.dcs.sherlock.api.model.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.util.*;

public class ModelProcessedResults {

	private long taskId;
	private IWorkspace workspace;
	private Class<? extends IDetector> detector;
	private IDetector.Rank rank;

	private List<ResultsInstance> data;

	public ModelProcessedResults(long taskId, IWorkspace workspace, Class<? extends IDetector> detector, IDetector.Rank rank) {
		this.taskId = taskId;
		this.workspace = workspace;
		this.detector = detector;
		this.rank = rank;
	}

	public Class<? extends IDetector> getDetector() {
		return detector;
	}

	public IDetector.Rank getRank() {
		return rank;
	}

	public long getTaskId() {
		return taskId;
	}

	public IWorkspace getWorkspace() {
		return workspace;
	}

	class ResultsInstance {

		private ISourceFile file;
		private DetectionType type;
		private float overallScore;

		private List<ResultsBlock> blocks;
	}

	class ResultsBlock {
		//String details; // Can be added if worthwhile????

		Tuple<Integer, Integer> parentBlock;
		float score;
		Map<ISourceFile, Tuple<Integer, Integer>> associatedBlocks;
	}

}
