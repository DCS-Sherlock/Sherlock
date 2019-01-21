package uk.ac.warwick.dcs.sherlock.engine.storage;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFileHelper;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;

import java.util.*;

public interface IStorageWrapper extends ISourceFileHelper {

	/**
	 * Shutdown the database
	 */
	void close();

	/**
	 * Create a new IWorkspace instance
	 *
	 * @param name name to create workspace under
	 * @param lang language of the workspace
	 *
	 * @return instance
	 */
	IWorkspace createWorkspace(String name, String lang);

	/**
	 * Fetches the class of ICodeBlockGroup used in this IStorageWrapper implementation
	 *
	 * @return class for ICodeBlockGroup
	 */
	Class<? extends ICodeBlockGroup> getCodeBlockGroupClass();

	/**
	 * @param ids workspace ids to fetch
	 *
	 * @return a list of all workspaces matching the passed ids in the database
	 */
	List<IWorkspace> getWorkspaces(List<Long> ids);

	/**
	 * Get all stored workspaces
	 *
	 * @return a list of all workspaces in the database
	 */
	List<IWorkspace> getWorkspaces();

	/**
	 * Store file in the database
	 *
	 * @param workspace   workspace to add file to
	 * @param filename    filename uploaded, used as the identifier to show to the user, identifying the file or files
	 * @param fileContent raw content of the file
	 */
	void storeFile(IWorkspace workspace, String filename, byte[] fileContent) throws WorkspaceUnsupportedException;

}