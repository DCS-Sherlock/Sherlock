package uk.ac.warwick.dcs.sherlock.api.common;

/**
 * Helper interface, for fetching {@link ISourceFile} instances from their unique id
 */
public interface ISourceFileHelper {

	/**
	 * Fetches instance of the {@link ISourceFile} for the unique id passed
	 * @param persistentId unique id of the file
	 * @return instance
	 */
	ISourceFile getSourceFile(long persistentId);

}
