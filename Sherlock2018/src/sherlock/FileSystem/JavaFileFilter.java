/**
 * 
 */
package sherlock.FileSystem;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Aliyah
 *
 */
public class JavaFileFilter implements FileFilter {

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
