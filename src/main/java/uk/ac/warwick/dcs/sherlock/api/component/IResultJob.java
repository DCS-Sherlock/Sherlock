package uk.ac.warwick.dcs.sherlock.api.component;

import java.util.*;

public interface IResultJob {

	IResultFile addFile(ISourceFile file);

	List<IResultFile> getFileResults();

	/**
	 * The unique id for the job result
	 *
	 * @return the unique id
	 */
	long getPersistentId();

	void remove();

	void store();

}
