package sherlock.extraction;

import java.io.File;

public interface ExtractionStrategy {
	/**
	 * @param dir	The input directory
	 * @return		A collection of files that are to be passed on to the parsing phase
	 */
	void extract(File[] dir);
}
