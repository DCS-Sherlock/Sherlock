package uk.ac.warwick.dcs.sherlock.engine.model;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

public interface IWorkspace {

	IJob createJob();

	List<ISourceFile> getFiles();

	List<IJob> getJobs();

	long getPersistentId();
}
