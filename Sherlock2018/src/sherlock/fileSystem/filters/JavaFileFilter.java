package sherlock.fileSystem.filters;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Aliyah
 *	Filters on whether the file is non hidden java file
 */
public class JavaFileFilter implements FileFilter {

	/* 
	 * Filters on whether the file is a file that ends with extension:
	 * 		.java
	 */
	@Override
	public boolean accept(File file) {
		if ( file.getName().endsWith(".java") ) {
			return !file.isHidden();
		}
		return false;
	}

}
