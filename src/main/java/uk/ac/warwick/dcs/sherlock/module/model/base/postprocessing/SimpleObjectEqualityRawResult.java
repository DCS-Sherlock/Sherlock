package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.model.data.IModelRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.data.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.PairedTuple;

import java.io.Serializable;
import java.util.*;

public class SimpleObjectEqualityRawResult<T extends Serializable> implements IModelRawResult {

	List<T> objects;
	List<PairedTuple<Integer, Integer, Integer, Integer>> locations;
	int size;

	public SimpleObjectEqualityRawResult(ISourceFile file1, ISourceFile file2, int numObjectsFile1, int numObjectsFile2) {
		this.objects = new ArrayList<>();
		this.locations = new ArrayList<>();

		this.size = 0;
	}

	public void put(T object, int file1Loc, int file2Loc) {
		this.put(object, file1Loc, file1Loc, file2Loc, file2Loc);
	}

	public void put(T object, int file1BlockStart, int file1BlockEnd, int file2BlockStart, int file2BlockEnd) {
		if (this.objects.size() != this.size || this.locations.size() != this.size) {
			//ERROR
			return;
		}

		this.objects.add(object);
		this.locations.add(new PairedTuple<>(file1BlockStart, file1BlockEnd, file2BlockStart, file2BlockEnd));
	}

	@Override
	public boolean testType(IModelRawResult baseline) {

		if (baseline instanceof SimpleObjectEqualityRawResult) {
			SimpleObjectEqualityRawResult bl = (SimpleObjectEqualityRawResult) baseline;

			//bl
		}

		return false;
	}
}
