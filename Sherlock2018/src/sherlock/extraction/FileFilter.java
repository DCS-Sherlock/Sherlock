package sherlock.extraction;

import java.io.File;

/**
 * @author Aliyah
 *
 * Filters on whether the file is a .java or .txt file
 */
class FileFilter implements java.io.FileFilter {

	/* 
	 * Filters on whether the file is a file that ends with extension:
	 * 		.java
	 * 		.txt
	 */
	@Override
	public boolean accept(File file) {
		if (file.getName().endsWith(".java") || file.getName().endsWith(".txt")) {
			return true;
		}
		// // Ensure the file is not a directory
		// if (file.isDirectory()) {
		// 	return false;
		// }
		
		return false;
	}

}