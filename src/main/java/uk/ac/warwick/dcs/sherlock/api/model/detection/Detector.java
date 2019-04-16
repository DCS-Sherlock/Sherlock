package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;

import java.util.*;

public abstract class Detector<T extends DetectorWorker> implements IDetector<T> {

	private String name;
	private String description;
	private List<PreProcessingStrategy> strategies;

	public Detector(String displayName, PreProcessingStrategy... preProcessingStrategies) {
		this(displayName, "", preProcessingStrategies);
	}

	public Detector(String displayName, String description, PreProcessingStrategy... preProcessingStrategies) {
		this.name = displayName;
		this.description = description;
		this.strategies = new LinkedList<>();
		Collections.addAll(this.strategies, preProcessingStrategies);
	}

	@Override
	public abstract List<T> buildWorkers(List<ModelDataItem> data);

	@Override
	public final String getDescription() {
		return this.description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	@Override
	public final String getDisplayName() {
		return this.name;
	}

	@Override
	public final List<PreProcessingStrategy> getPreProcessors() {
		return this.strategies;
	}
}
