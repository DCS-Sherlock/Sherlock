/**
 * 
 */
package sherlock.extraction;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;

import sherlock.extraction.ExtractionContext;

/**
 * @author Aliyah
 *
 */
public class DirectoryProcessor {
	private File dir ;
	
	DirectoryFilter dirfilter = new DirectoryFilter();
	ZipFilenameFilter zipfilter = new ZipFilenameFilter();
	GZipFilenameFilter gzipfilter = new GZipFilenameFilter();
	FileFilter filefilter = new FileFilter();
	
	public DirectoryProcessor(File dir, String sourceDirectoryName){
		this.dir = dir ;
		processDirectory(sourceDirectoryName);
	}
	
	/**
	 * ProcessDirectory method which extracts all files from different types of compressed files.
	 */
	private void processDirectory(String sourceDirectoryName){
		// Create the source directory in the Sherlock directory
		// Create the Preprocessing directory
		// Create the Original directory - this is where the files are to be stored
		String destination = System.getProperty("user.home") + File.separator + "Sherlock" + File.separator + sourceDirectoryName + File.separator + "preprocessing" + File.separator + "Original";
		if ( ! new File(destination).exists() ) {
			if (new File(destination).mkdirs() ) {
				System.out.println("Success Making " + destination + " Directory");
			} else {
				System.out.println("Failed to create directory!");
			}
		} else {
			System.out.println("Directory Exists");
		}
		
		System.out.println("Source Destination" + destination);
		File[] files = dir.listFiles(filefilter);
		
		// For each file in the selected directory, copy it to the Sherlock Directory using the same name
		for (File f : files) {
			Path source = f.toPath();
     		Path dest = (new File(destination)).toPath();

			try {
				Files.copy(source, dest.resolve(f.getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch ( FileAlreadyExistsException ae ){
				System.out.println("The file already exists in "+ destination);
			} catch (IOException e) {
				System.out.println("Unable to copy File to "+ destination);
				e.printStackTrace();
			} 
		}
		
		System.out.println(files.toString());
		File[] zipfiles = dir.listFiles(zipfilter);		// Get all the zip files
		File[] gzipfiles = dir.listFiles(gzipfilter);	// Get all the gzip files
		
		// If there are zipped files that need extracting, extract them
		if ( zipfiles.length > 0 ) {
			System.out.println("Got some zipped files");
			ExtractionContext es_zip = new ExtractionContext(new ZipHandler(), zipfiles, destination);
		}
		// If there are gzipped files that need extracting, extract them
		if ( gzipfiles.length > 0 ) {
			System.out.println("Got some Gzipped files");
			ExtractionContext es_gzip = new ExtractionContext(new GZipHandler(), gzipfiles, destination);
		}
		System.out.println(System.getProperty("user.home"));
	}
	
	
}
