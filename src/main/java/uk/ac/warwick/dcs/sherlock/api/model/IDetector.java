package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.logging.Logger;

import java.util.stream.Stream;

public interface IDetector {

	/**
	 * Main execution method for the detector
	 *
	 * @param data   Data set to run detection on
	 * @param result Result set, append data to this
	 *
	 * @return exit status, 0 for okay. Return value is passed to processExitStatus method if > 0
	 */
	int execute(IModelData data, IModelResult result);

	/**
	 * @return UI display name of the algorithm
	 */
	String getDisplayName();

	/**
	 * Returns the appropriate lexer for this strategy and the language of the source files
	 * <p>
	 * TODO: on return check that lexer conforms to the channel spec we defined, throw exception if it does not
	 *
	 * @param lang the language of the source files
	 *
	 * @return the lexer class to use
	 */
	Class<? extends org.antlr.v4.runtime.Lexer> getLexer(Language lang);

	/**
	 * Allows implementation to override the default post processor if a different matching scheme is required
	 *
	 * @return the post processor to use, default null (triggers default scheme, overlapping pairs matching)
	 */
	default Class<? extends IPostProcessor> getPostProcessor() {
		return null;
	}

	/**
	 * Allows implementation to specify preprocessors required. The full file set data processed with each of the returned preprocessors will be made available to the implementation via IModelData
	 *
	 * @return returns a stream of IPreProcessor Classes. Example: "return Stream.of(preRemoveWhiteSpace.class, preRemoveComments.class)";
	 */
	Stream<Class<? extends IPreProcessor>> getPreProcessors();

	/**
	 * @return Stream of the languages supported by the algorithm
	 */
	Stream<Language> getSupportedLanguages();

	/**
	 * Method to tokenise the exit status returned by the
	 *
	 * @param exitStatus Implementation execution exit status
	 */
	default void processExitStatus(int exitStatus) {
		Logger.log("Detector error occurred. [Default MSG]");
	}

}
