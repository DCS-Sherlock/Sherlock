package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.SherlockHelper;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.util.PairedTuple;

import java.io.Serializable;
import java.util.*;

public class SimpleObjectEqualityRawResult<T extends Serializable> extends AbstractModelTaskRawResult {

	private long file1id;
	private long file2id;

	private int file1NumObjs;
	private int file2NumObjs;

	private List<T> objects;
	private List<PairedTuple<Integer, Integer, Integer, Integer>> locations;

	private int size;

	public SimpleObjectEqualityRawResult(ISourceFile file1, ISourceFile file2, int numObjectsFile1, int numObjectsFile2) {
		this.file1id = file1.getPersistentId();
		this.file2id = file2.getPersistentId();

		this.file1NumObjs = numObjectsFile1;
		this.file2NumObjs = numObjectsFile2;

		this.objects = new ArrayList<>();
		this.locations = new ArrayList<>();

		this.size = 0;
	}

	public ISourceFile getFile1() {
		return SherlockHelper.getSourceFile(file1id);
	}

	public int getFile1NumObjects() {
		return file1NumObjs;
	}

	public ISourceFile getFile2() {
		return SherlockHelper.getSourceFile(file2id);
	}

	public int getFile2NumObjects() {
		return file2NumObjs;
	}

	public PairedTuple<Integer, Integer, Integer, Integer> getLocation(int index) {
		return this.locations.get(index);
	}

	public List<PairedTuple<Integer, Integer, Integer, Integer>> getLocations() {
		return this.locations;
	}

	public T getObject(int index) {
		return this.objects.get(index);
	}

	public List<T> getObjects() {
		return this.objects;
	}

	public int getSize() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}

	public void put(T object, int file1Loc, int file2Loc) {
		this.put(object, file1Loc, file1Loc, file2Loc, file2Loc);
	}

	public void put(T object, int file1BlockStart, int file1BlockEnd, int file2BlockStart, int file2BlockEnd) {
		if (this.objects.size() != this.size || this.locations.size() != this.size) {
			System.out.println("not sized");
			return;
		}

		this.objects.add(object);
		this.locations.add(new PairedTuple<>(file1BlockStart, file1BlockEnd, file2BlockStart, file2BlockEnd));
		this.size++;
	}

	@Override
	public boolean testType(AbstractModelTaskRawResult baseline) {
		if (baseline instanceof SimpleObjectEqualityRawResult) {
			SimpleObjectEqualityRawResult bl = (SimpleObjectEqualityRawResult) baseline;
			return bl.getObjects().get(0).getClass().equals(this.getObjects().get(0).getClass()); // Check generic type is the same
		}

		return false;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(this.getFile1().getFileDisplayName()).append(" vs ").append(this.getFile2().getFileDisplayName()).append("\n\r");
		for (int i = 0; i < this.size; i++) {
			str.append(this.objects.get(i).toString()).append(" - ").append(this.locations.get(i).toString()).append("\n\r");
		}
		return str.toString();
	}
}
