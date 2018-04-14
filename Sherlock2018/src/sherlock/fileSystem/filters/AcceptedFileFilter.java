package sherlock.fileSystem.filters;

import java.io.File;

/**
 * @author Aliyah
 *
 * Filter to collect all files that are accepted by the Sherlock Plagiarism detector
 * Filters on whether the file is a .java or .txt file. Hidden files are not accepted.
 */
public class AcceptedFileFilter implements java.io.FileFilter {

	/* 
	 * Filters on whether the file is a file that ends with extension:
	 * 		.java
	 * 		.txt
	 */
	@Override
	public boolean accept(File file) {
		if (file.getName().endsWith(".java") || file.getName().endsWith(".txt")) {
			if ( !file.isHidden()) {
				return true;
			}
		}
		
		return false;
	}

}