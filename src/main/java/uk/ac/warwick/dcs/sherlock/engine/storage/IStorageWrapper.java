package uk.ac.warwick.dcs.sherlock.engine.storage;

import uk.ac.warwick.dcs.sherlock.api.util.ISourceFileHelper;
import uk.ac.warwick.dcs.sherlock.engine.model.IWorkspace;

public interface IStorageWrapper extends ISourceFileHelper {

	void close();

	void storeFile(String filename, byte[] fileContent);

	IWorkspace createWorkspace();

}
