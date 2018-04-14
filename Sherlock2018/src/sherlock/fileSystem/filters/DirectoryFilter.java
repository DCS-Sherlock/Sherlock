package sherlock.fileSystem.filters;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Aliyah
 *	Filters on whether the file is a directory
 */
public class DirectoryFilter implements FileFilter {

	@Override
	public boolean accept(File name) {
		return name.isDirectory();
	}
}
