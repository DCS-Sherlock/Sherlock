package uk.ac.warwick.dcs.sherlock.engine.component;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

public interface ISubmission {

	int getTotalFileCount();

	List<ISourceFile> getContainedFiles();

	long getId();

	List<ISubmission> getContainedDirectories();

	String getName();

}
