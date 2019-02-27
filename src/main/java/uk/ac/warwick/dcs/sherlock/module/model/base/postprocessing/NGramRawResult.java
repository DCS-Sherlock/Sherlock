package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.SherlockHelper;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.PairedTuple;

import java.io.Serializable;
import java.util.*;

/**
 * Stores the set of match objects for a pair of inputted files.
 * @param <T> N-Gram match object comparing similarity data between 2 code blocks
 */
public class NGramRawResult<T extends Serializable> extends AbstractModelTaskRawResult {

	/**
	 * The ID number of the first file in the compared pair.
	 */
	long file1id;
	/**
	 * The ID number of the second file in the compared pair.
	 */
	long file2id;

	/**
	 * The list of match objects (containers).
	 */
	List<T> objects;
	/**
	 * The list of file block locations. Stored in the form of 2 pairs, each pair denoting the start and end line of
	 * the code block in the respective file. Stored in form List &lt;PairedTuple&lt;Integer, Integer, Integer, Integer&gt;&gt;.
	 */
	List<PairedTuple<Integer, Integer, Integer, Integer>> locations;

	/**
	 * The number of match objects stored in the container
	 */
	int size;

	/**
	 * Object constructor, saves the compared file ids, initialises interior lists as ArrayLists, and sets size to zero.
	 * @param file1 File ID of the first file in the compared pair.
	 * @param file2 File ID of the second file in the compared pair.
	 */
	public NGramRawResult(ISourceFile file1, ISourceFile file2) {
		this.file1id = file1.getPersistentId();
		this.file2id = file2.getPersistentId();

		this.objects = new ArrayList<>();
		this.locations = new ArrayList<>();

		this.size = 0;
	}

	/**
	 * Getter for ID of first file in comparison pair.
	 * @return First ID.
	 */
	public ISourceFile getFile1() {
		return SherlockHelper.getSourceFile(file1id);
	}

	/**
	 * Getter for ID of second file in comparison pair.
	 * @return Second ID.
	 */
	public ISourceFile getFile2() {
		return SherlockHelper.getSourceFile(file2id);
	}

	/**
	 * Getter for the block location indexes in the order: File1 start, File1 end, File2 start, File2 end.
	 * @return Block location indexes.
	 */
	public List<PairedTuple<Integer, Integer, Integer, Integer>> getLocations() {
		return this.locations;
	}

	/**
	 * Getter for the number of matches stored in the object.
	 * @return The number of matches stored in the object.
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Returns true if no matches in object, false otherwise.
	 * @return True if no matches in object, false otherwise.
	 */
	@Override
	public boolean isEmpty() {
		return this.size <= 0;
	}

	/**
	 * Method to store a matched block where both file blocks are a single line.
	 * @param object The mach object for the matched pair.
	 * @param file1Loc The line number of the File1 block.
	 * @param file2Loc The line number of the File2 block.
	 */
	public void put(T object, int file1Loc, int file2Loc) {
		this.put(object, file1Loc, file1Loc, file2Loc, file2Loc);
	}

	/**
	 * Method to store a matched block.
	 * @param object The match object for the matched pair.
	 * @param file1BlockStart The start line of the block in File1.
	 * @param file1BlockEnd The end line of the block in File1.
	 * @param file2BlockStart The start line of the block in File2.
	 * @param file2BlockEnd The end line of the block in File2.
	 */
	public void put(T object, int file1BlockStart, int file1BlockEnd, int file2BlockStart, int file2BlockEnd) {
		if (this.objects.size() != this.size || this.locations.size() != this.size) {
			System.out.println("not sized");
			return;
		}

		this.objects.add(object);
		this.locations.add(new PairedTuple<>(file1BlockStart, file1BlockEnd, file2BlockStart, file2BlockEnd));
		this.size++;
	}

	/**
	 * Verifies that the inputted RawResult type is the same type as the current object.
	 * @param baseline the baseline object, in the set, current instance must be of the same exact type as this.
	 * @return True if input is same object type as current object, false otherwise.
	 */
	@Override
	public boolean testType(AbstractModelTaskRawResult baseline) {
		if (baseline instanceof NGramRawResult) {
			NGramRawResult bl = (NGramRawResult) baseline;
			return bl.getObjects().get(0).getClass().equals(this.getObjects().get(0).getClass()); // Check generic type is the same
		}

		return false;
	}

	/**
	 * Get the list of match objects within the container.
	 * @return The list of match objects within the container.
	 */
	public List<T> getObjects() {
		return this.objects;
	}

	/**
	 * Returns the string form of the list of stored objects along with their locations in their respective files.
	 * @return The string form of the list of stored objects along with their locations in their respective files.
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < this.size; i++) {
			str.append(this.objects.get(i).toString()).append(" - ").append(this.locations.get(i).toString()).append("\n");
		}
		return str.toString();
	}
}
