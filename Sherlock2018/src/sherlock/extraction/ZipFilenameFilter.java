package sherlock.extraction;

import java.io.File;
import java.io.FilenameFilter;

public class ZipFilenameFilter implements FilenameFilter {
	/* 
	 * 	Return true if and only if file ends with one of the extension names:
	 * 	zip
	 * 	ZIP.
	 */
	@Override
	public boolean accept(File dir, String name) {
		File f = new File(dir, name);
	    FileFilter ff = new FileFilter();

	    return (!ff.accept(f) && !f.isDirectory()						// File is not a regular file and is not a directory
	            && (name.endsWith(".zip") || name.endsWith("ZIP")));		// and ends with .zip or .ZIP extension
	}
	
}
