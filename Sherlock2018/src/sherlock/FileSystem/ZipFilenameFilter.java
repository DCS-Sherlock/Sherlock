package sherlock.FileSystem;

import java.io.File;
import java.io.FilenameFilter;

class ZipFilenameFilter implements FilenameFilter {
	/* 
	 * 	Return true if and only if file ends with one of the extension names:
	 * 	zip
	 * 	ZIP.
	 */
	@Override
	public boolean accept(File dir, String name) {
		File f = new File(dir, name);
	    AcceptedFileFilter ff = new AcceptedFileFilter();

	    return (!ff.accept(f) && !f.isDirectory()						// File is not a regular file and is not a directory
	            && (name.endsWith(".zip") || name.endsWith("ZIP")));		// and ends with .zip or .ZIP extension
	}
	
}
