package uk.ac.warwick.dcs.sherlock.model.base.data;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IModelResult;
import uk.ac.warwick.dcs.sherlock.api.model.IPairedBlocks;

import java.util.LinkedList;
import java.util.List;

public class TestModelResult implements IModelResult {

	private List<ISourceFile> files;
	private Class<? extends IDetector> detector;

	private List<IPairedBlocks> pairedBlocks;

	public TestModelResult(List<ISourceFile> files, Class<? extends IDetector> detector) {
		this.files = files;
		this.detector = detector;

		this.pairedBlocks = new LinkedList<>();
	}

	@Override
	public void addPairedBlocks(IPairedBlocks blockPair) {
		this.pairedBlocks.add(blockPair);
	}

	@Override
	public List<IPairedBlocks> getAllPairedBlocks() {
		return this.pairedBlocks;
	}

	@Override
	public Class<? extends IDetector> getDetector() {
		return this.detector;
	}

	@Override
	public List<ISourceFile> getIncludedFiles() {
		return this.files;
	}
}
