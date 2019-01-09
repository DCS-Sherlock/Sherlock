package uk.ac.warwick.dcs.sherlock.engine.storage;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFileHelper;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelProcessedResults;
import uk.ac.warwick.dcs.sherlock.engine.model.IWorkspace;

public interface IStorageWrapper extends ISourceFileHelper {

	void close();

	void storeFile(String filename, byte[] fileContent);

	IWorkspace createWorkspace();

	Class<? extends IModelProcessedResults> getModelProcessedResultsClass();

}
