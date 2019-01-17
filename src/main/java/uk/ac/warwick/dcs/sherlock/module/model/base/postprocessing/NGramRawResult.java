package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.SherlockHelper;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.PairedTuple;

import java.io.Serializable;
import java.util.*;

public class NGramRawResult<T extends Serializable> extends AbstractModelTaskRawResult {

	long file1id;
	long file2id;

	List<T> objects;
	List<PairedTuple<Integer, Integer, Integer, Integer>> locations;

	int size;

	public NGramRawResult(ISourceFile file1, ISourceFile file2) {
		this.file1id = file1.getPersistentId();
		this.file2id = file2.getPersistentId();

		this.objects = new ArrayList<>();
		this.locations = new ArrayList<>();

		this.size = 0;
	}

	public ISourceFile getFile1() {
		return SherlockHelper.getSourceFile(file1id);
	}

	public ISourceFile getFile2() {
		return SherlockHelper.getSourceFile(file2id);
	}

	public List<PairedTuple<Integer, Integer, Integer, Integer>> getLocations() {
		return this.locations;
	}

	public int getSize() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		return this.size <= 0;
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
		if (baseline instanceof NGramRawResult) {
			NGramRawResult bl = (NGramRawResult) baseline;
			return bl.getObjects().get(0).getClass().equals(this.getObjects().get(0).getClass()); // Check generic type is the same
		}

		return false;
	}

	public List<T> getObjects() {
		return this.objects;
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
