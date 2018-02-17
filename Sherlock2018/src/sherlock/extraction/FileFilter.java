package sherlock.extraction;

import java.io.File;

/**
 * @author Aliyah
 *
 */
class FileFilter implements java.io.FileFilter {

	/* 
	 * 
	 */
	@Override
	public boolean accept(File file) {
		if (file.getName().endsWith(".java") || file.getName().endsWith(".txt")) {
			return true;
		}
		// make sure the file is not a directory
		if (file.isDirectory()) {
			return false;
		}
		
		return false;
	}

}