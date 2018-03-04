package sherlock.FileSystem.Filters;

import java.io.File;

/**
 * @author Aliyah
 *
 * Filter to collect all Source code files that are accepted by the Sherlock Plagiarism detector
 * Filters on whether the file is a .java file
 */
public class SourceCodeFilter implements java.io.FileFilter {

	/* 
	 * Filters on whether the file is a file that ends with extension:
	 * 		.java
	 */
	@Override
	public boolean accept(File file) {
		if ( file.getName().endsWith(".java") ) {
			if ( !file.isHidden()) {
				return true;
			}
		}
		
		return false;
	}

}