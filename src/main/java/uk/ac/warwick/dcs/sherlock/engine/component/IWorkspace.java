package uk.ac.warwick.dcs.sherlock.engine.component;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;

import java.util.*;

public interface IWorkspace {

	/**
	 * Creates a new job instance
	 *
	 * @return the new job
	 */
	IJob createJob();

	/**
	 * @return the list of files currently associated with the workspace
	 */
	List<ISourceFile> getFiles();

	/**
	 * @return the list of submissions to the workspace
	 */
	List<ISubmission> getSubmissions();

	/**
	 * @return list of job history
	 */
	List<IJob> getJobs();

	/**
	 * @return the language for the workspace
	 */
	String getLanguage();

	/**
	 * @param lang set the workspace to use this language
	 */
	void setLanguage(String lang);

	/**
	 * @return the name of the workspace
	 */
	String getName();

	/**
	 * @param name set the workspace name to this
	 */
	void setName(String name);

	/**
	 * @return the unique id for the workspace
	 */
	long getPersistentId();

	/**
	 * Remove the workspace, and all of the files and results associated, cannot be undone
	 */
	void remove();

}
