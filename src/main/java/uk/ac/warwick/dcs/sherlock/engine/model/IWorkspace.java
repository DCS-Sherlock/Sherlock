package uk.ac.warwick.dcs.sherlock.engine.model;

		import java.util.*;

public interface IWorkspace {

	IJob createJob();

	List<IJob> getJobs();

}
