package uk.ac.warwick.dcs.sherlock.engine.storage;

import uk.ac.warwick.dcs.sherlock.api.model.data.IModelProcessedResult;
import uk.ac.warwick.dcs.sherlock.engine.model.IWorkspace;

public interface IStorageWrapper {

	void close();

	void storeFile(String filename, byte[] fileContent);

	IWorkspace createWorkspace();

	Class<? extends IModelProcessedResult> getModelProcessedResultsClass();

}
