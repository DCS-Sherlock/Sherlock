package uk.ac.warwick.dcs.sherlock.engine.component;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

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
	 * @return list of job history
	 */
	List<IJob> getJobs();

	/**
	 * @return the language for the workspace
	 */
	Language getLanguage();

	/**
	 * @param lang set the workspace to use this language
	 */
	void setLanguage(Language lang);

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

}
