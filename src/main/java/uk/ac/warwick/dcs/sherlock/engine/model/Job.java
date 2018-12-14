package uk.ac.warwick.dcs.sherlock.engine.model;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.data.IWorkspace;

import java.util.*;

public class Job {

	private long taskId;
	private IWorkspace workspace;
	private Class<? extends IDetector> detector;
	private IDetector.Rank rank;

	//When adding check that all same type
	private List<IModelRawResult> rawResults;

	public Job(long taskId, IWorkspace workspace, Class<? extends IDetector> detector, IDetector.Rank rank) {
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

}
