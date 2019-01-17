package uk.ac.warwick.dcs.sherlock.engine.storage;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFileHelper;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
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
	 * @return instance
	 */
	IWorkspace createWorkspace(String name, Language lang);

	/**
	 * Fetches the class of ICodeBlockGroup used in this IStorageWrapper implementation
	 *
	 * @return
	 */
	Class<? extends ICodeBlockGroup> getCodeBlockGroupClass();

	/**
	 * @param ids workspace ids to fetch
	 *
	 * @return a list of all workspaces matching the passed ids in the database
	 */
	List<IWorkspace> getWorkspaces(List<Long> ids);

	/**
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
