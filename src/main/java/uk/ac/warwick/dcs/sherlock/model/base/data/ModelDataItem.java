package uk.ac.warwick.dcs.sherlock.model.base.data;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFileLine;
import uk.ac.warwick.dcs.sherlock.api.model.IModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelDataItem implements IModelDataItem {

	private ISourceFile file;

	//TODO: temporary, migrate to real data structure
	private Map<Class<? extends IPreProcessor>, List<ISourceFileLine>> mapping;

	public ModelDataItem(ISourceFile file) {
		this.file = file;
		this.mapping = new HashMap<>();
	}

	public void addPreProcessedLines(Class<? extends IPreProcessor> proc, List<ISourceFileLine> lines) {
		this.mapping.put(proc, lines);
	}

	@Override
	public ISourceFile getFile() {
		return this.file;
	}

	@Override
	public List<ISourceFileLine> getPreProcessedLines(Class<? extends IPreProcessor> proc) {
		return this.mapping.get(proc);
	}
}
