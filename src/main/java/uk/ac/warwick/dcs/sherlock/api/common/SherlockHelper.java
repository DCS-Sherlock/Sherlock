package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.data.IModelProcessedResults;
import uk.ac.warwick.dcs.sherlock.api.model.data.ISourceFile;

public class SherlockHelper {

	private static ISourceFileHelper sourceFileHelper;
	private static Class<? extends IModelProcessedResults> modelProcessedResultsClass;

	public static IModelProcessedResults getModelProcessedResultInstance() throws IllegalAccessException, InstantiationException {
		return modelProcessedResultsClass.newInstance();
	}

	public static ISourceFile getSourceFile(long persistentId) {
		return sourceFileHelper.getSourceFile(persistentId);
	}

}
