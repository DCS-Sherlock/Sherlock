package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.Arrays;
import java.util.List;

public interface IPreProcessingStrategy {

	static IPreProcessingStrategy of(String name, IPreProcessor... preProcessor) {
		return new GenericPreProcessingStrategy(name, false, Arrays.asList(preProcessor));
	}

	static IPreProcessingStrategy of(String name, boolean tokenise, IPreProcessor... preProcessor) {
		return new GenericPreProcessingStrategy(name, tokenise, Arrays.asList(preProcessor));
	}

	String getName();

	List<IPreProcessor> getPreProcessors();

	default ITokeniser getTokeniser() {
		return null;
	}

	boolean isResultTokenised();

	class GenericPreProcessingStrategy implements IPreProcessingStrategy {

		private String name;
		private boolean tokenise;
		private List<IPreProcessor> preProcessors;

		private GenericPreProcessingStrategy(String name, boolean tokenise, List<IPreProcessor> preProcessors) {
			this.name = name;
			this.tokenise = tokenise;
			this.preProcessors = preProcessors;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public List<IPreProcessor> getPreProcessors() {
			return this.preProcessors;
		}

		@Override
		public boolean isResultTokenised() {
			return this.tokenise;
		}
	}

}
