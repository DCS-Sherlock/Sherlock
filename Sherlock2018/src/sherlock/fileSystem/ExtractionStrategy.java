package sherlock.fileSystem;

import java.io.File;

public interface ExtractionStrategy {

	/**
	 * 	The extraction process
	 * @param dir 			- The file to be extracted
	 * @param destination	- The destination the files are to extracted to
	 */
	void extract(File[] dir, String destination);
}
