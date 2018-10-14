package uk.ac.warwick.dcs.sherlock.model.base.data;

import uk.ac.warwick.dcs.sherlock.api.model.IModelResultItem;
import uk.ac.warwick.dcs.sherlock.api.model.IPairedBlocks;

import java.util.LinkedList;
import java.util.List;

public class ModelResultItem implements IModelResultItem {

	private List<IPairedBlocks> pairedBlocks;

	public ModelResultItem() {
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
}
