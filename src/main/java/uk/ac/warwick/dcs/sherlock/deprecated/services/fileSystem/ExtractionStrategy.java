package uk.ac.warwick.dcs.sherlock.deprecated.services.fileSystem;

import java.io.File;

interface ExtractionStrategy {

	/**
	 * The extraction tokenise
	 *
	 * @param dir         - The file to be extracted
	 * @param destination - The destination the files are to extracted to
	 */
	void extract(File[] dir, String destination);
}
