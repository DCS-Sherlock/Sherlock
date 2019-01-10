package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.ISourceFileHelper;

public class SherlockHelper {

	private static ISourceFileHelper sourceFileHelper;

	public static ISourceFile getSourceFile(long persistentId) {
		return sourceFileHelper.getSourceFile(persistentId);
	}

}
