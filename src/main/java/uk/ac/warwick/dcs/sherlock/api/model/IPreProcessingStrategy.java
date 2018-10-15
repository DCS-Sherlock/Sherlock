package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.Arrays;
import java.util.List;

public interface IPreProcessingStrategy {

	static IPreProcessingStrategy of(String name, Class<? extends IPreProcessor>... preProcessor) {
		return new GenericPreProcessingStrategy(name, false, Arrays.asList(preProcessor));
	}

	static IPreProcessingStrategy of(String name, boolean tokenise, Class<? extends IPreProcessor>... preProcessor) {
		return new GenericPreProcessingStrategy(name, tokenise, Arrays.asList(preProcessor));
	}

	String getName();

	List<Class<? extends IPreProcessor>> getPreProcessorClasses();

	default ITokenStringifier getStringifier() {
		return null;
	}

	class GenericPreProcessingStrategy implements IPreProcessingStrategy {

		private String name;
		private boolean tokenise;
		private List<Class<? extends IPreProcessor>> preProcessors;

		private GenericPreProcessingStrategy(String name, boolean tokenise, List<Class<? extends IPreProcessor>> preProcessors) {
			this.name = name;
			this.tokenise = tokenise;
			this.preProcessors = preProcessors;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public List<Class<? extends IPreProcessor>> getPreProcessorClasses() {
			return this.preProcessors;
		}

		public boolean isResultTokenised() {
			return this.tokenise;
		}
	}

}
