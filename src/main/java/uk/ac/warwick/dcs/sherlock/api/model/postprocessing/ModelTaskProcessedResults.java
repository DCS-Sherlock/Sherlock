package uk.ac.warwick.dcs.sherlock.api.model.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.component.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.util.SherlockHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.*;

/**
 * Processed results for a task (for a single IDetector instance)
 * <p>
 * Each file in this should be scored for its performance only in this task
 */
public class ModelTaskProcessedResults {

	private Map<ISourceFile, Integer> totals;
	private List<ICodeBlockGroup> groups;

	/**
	 * default constructor
	 */
	public ModelTaskProcessedResults() {
		this.groups = new LinkedList<>();
		this.totals = null;
	}

	/**
	 * Creates a new ICodeBlockGroup instance, adds it to the results list and returns it
	 *
	 * @return the new instance
	 */
	public ICodeBlockGroup addGroup() {
		try {
			ICodeBlockGroup g = SherlockHelper.getInstanceOfCodeBlockGroup();
			this.groups.add(g);
			return g;
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace(); // couldn't create group
		}

		return null;
	}

	/**
	 * Remove empty groups from the list, or ones with an unknown detection type
	 *
	 * @return returns true if one of the groups does not have a detection type set
	 *
	 * @throws UnknownDetectionTypeException detection type is not registered
	 */
	public boolean cleanGroups() throws UnknownDetectionTypeException {
		this.groups = this.groups.stream().filter(ICodeBlockGroup::isPopulated).collect(Collectors.toList());

		for (ICodeBlockGroup g : this.groups) {
			if (g.getDetectionType() == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the scoring total for the file passed, by default this is the total number of lines in the file. Can be set to a custom value via the totals map if appropriate
	 *
	 * @param file the file to get total for
	 *
	 * @return the total used for scoring
	 */
	public int getFileTotal(ISourceFile file) {
		if (this.totals != null) {
			if (this.totals.containsKey(file)) {
				return this.totals.get(file);
			}
		}

		return file.getTotalLineCount();
	}

	/**
	 * Fetches a list of groups containing the passed files
	 *
	 * @param file1 file present in all groups
	 * @param file2 file present in all groups
	 *
	 * @return list of groups
	 */
	public List<ICodeBlockGroup> getGroups(ISourceFile file1, ISourceFile file2) {
		return groups.stream().filter(g -> g.filePresent(file1) && g.filePresent(file2)).collect(Collectors.toList());
	}

	/**
	 * Gets the {@link ICodeBlockGroup} instances in this result
	 *
	 * @return the final list of {@link ICodeBlockGroup} produced by the task
	 */
	public List<ICodeBlockGroup> getGroups() {
		return groups;
	}

	/**
	 * Fetches a list of groups containing the passed file
	 *
	 * @param file file present in all groups
	 *
	 * @return list of groups
	 */
	public List<ICodeBlockGroup> getGroups(ISourceFile file) {
		return groups.stream().filter(g -> g.filePresent(file)).collect(Collectors.toList());
	}

	/**
	 * Remove a group from the list of groups
	 *
	 * @param group group to remove
	 */
	public void removeGroup(ICodeBlockGroup group) {
		if (group != null) {
			this.groups.remove(group);
		}
	}

	/**
	 * Set the file to integer map for the scoring totals, which are used to calculate the percentage of a file taken by each block, for example this is set to the number of variables in a file for
	 * the variable detector to score against this.
	 * <p>
	 * By default the total is set to the line count for a file
	 *
	 * @param totalsMap the mapping to use instead of the default.
	 */
	public void setFileTotals(Map<ISourceFile, Integer> totalsMap) {
		this.totals = totalsMap;
	}
}
