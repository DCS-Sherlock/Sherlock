package uk.ac.warwick.dcs.sherlock.api.model;

public interface IPreProcessor {

	/**
	 * Allows the pre processor to specify a dependency which should first be run on the data before it is passed to this preprocessor
	 * @return the dependency
	 */
	default Class<? extends IPreProcessor> getDependencies() {
		return null;
	}

	// TODO: look at the datatypes to be passed around for file inputs

}
