package sherlock.extraction;

import java.io.File;

public interface ExtractionStrategy {

	/**
	 * @param dir 			- The file to be extracted
	 * @param destination	- The destination the files are to extracted to
	 */
	void extract(File[] dir, String destination);
}
