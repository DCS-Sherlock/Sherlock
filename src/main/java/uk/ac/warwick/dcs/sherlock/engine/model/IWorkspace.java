package uk.ac.warwick.dcs.sherlock.engine.model;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import java.util.*;

public interface IWorkspace {

	IJob createJob();

	List<ISourceFile> getFiles();

	List<IJob> getJobs();

	Language getLanguage();

	void setLanguage(Language lang);

	String getName();

	void setName(String name);

	long getPersistentId();

}
