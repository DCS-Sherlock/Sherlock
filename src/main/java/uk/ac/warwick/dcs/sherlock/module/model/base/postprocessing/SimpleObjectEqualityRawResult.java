package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.model.data.AbstractModelRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.data.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.PairedTuple;

import java.io.Serializable;
import java.util.*;

public class SimpleObjectEqualityRawResult<T extends Serializable> extends AbstractModelRawResult {

	long file1id;
	long file2id;

	int file1NumObjs;
	int file2NumObjs;

	List<T> objects;
	List<PairedTuple<Integer, Integer, Integer, Integer>> locations;

	int size;

	public SimpleObjectEqualityRawResult(ISourceFile file1, ISourceFile file2, int numObjectsFile1, int numObjectsFile2) {
		this.file1id = file1.getPersistentId();
		this.file2id = file2.getPersistentId();

		this.file1NumObjs = numObjectsFile1;
		this.file2NumObjs = numObjectsFile2;

		this.objects = new ArrayList<>();
		this.locations = new ArrayList<>();

		this.size = 0;
	}

	public List<T> getObjects() {
		return this.objects;
	}

	public int getSize() {
		return this.size;
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
	public boolean testType(AbstractModelRawResult baseline) {
		if (baseline instanceof SimpleObjectEqualityRawResult) {
			SimpleObjectEqualityRawResult bl = (SimpleObjectEqualityRawResult) baseline;
			return bl.getObjects().get(0).getClass().equals(this.getObjects().get(0).getClass());
		}

		return false;
	}

	@Override
	public boolean isEmpty() {
		return this.size <= 0;
	}



	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < this.size; i++) {
			str.append(this.objects.get(i).toString()).append(" - ").append(this.locations.get(i).toString()).append("\n");
		}
		return str.toString();
	}
}