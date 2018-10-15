package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.Arrays;
import java.util.List;

public interface IPreProcessingStrategy {

	static IPreProcessingStrategy of(IPreProcessor... preProcessor) {
		return new GenericPreProcessingStrategy(false, Arrays.asList(preProcessor));
	}

	static IPreProcessingStrategy of(boolean tokenise, IPreProcessor... preProcessor) {
		return new GenericPreProcessingStrategy(tokenise, Arrays.asList(preProcessor));
	}

	List<IPreProcessor> getPreProcessors();

	boolean isResultTokenised();

	default ITokeniser getTokeniser() {
		return null;
	}

	class GenericPreProcessingStrategy implements IPreProcessingStrategy {

		private boolean tokenise;
		private List<IPreProcessor> preProcessors;

		private GenericPreProcessingStrategy(boolean tokenise, List<IPreProcessor> preProcessors) {
			this.tokenise = tokenise;
			this.preProcessors = preProcessors;
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
