package uk.ac.warwick.dcs.sherlock.services.fileSystem.filters;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Aliyah
 *	Filters on whether the file is a plain text file
 */
public class PlainTextFilter implements FileFilter {

	/* 
	 * Filters on whether the file is a file that is not hidden and ends with extension:
	 * 		.txt
	 */
	@Override
	public boolean accept(File file) {
		if ( file.getName().endsWith(".txt") ) {
			return !file.isHidden();
		}
		return false;
	}

}
