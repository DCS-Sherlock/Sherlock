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
public class PlainTextFilter implements FileFilter {

	/* 
	 * Filters on whether the file is a file that ends with extension:
	 * 		.txt
	 */
	@Override
	public boolean accept(File file) {
		if ( file.getName().endsWith(".txt") ) {
			if ( !file.isHidden()) {
				return true;
			}
		}
		return false;
	}

}
