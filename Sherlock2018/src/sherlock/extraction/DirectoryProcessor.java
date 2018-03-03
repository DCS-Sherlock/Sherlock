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
 *	Processes the directory input by extracting any files from compressed files and copying them into the Sherlock 
 *	Directory that exists in the Users home directory. 
 */
public class DirectoryProcessor {
	
	/**
	 * The directory to be processed by this instance
	 */
	private File dir ;
	
	/*
	 * A filter that determines whether a file object is a directory
	 */
	DirectoryFilter dirfilter = new DirectoryFilter();
	
	/**
	 * A filter that determines whether a file object is a zip object
	 * that ends with extension:
	 * 		.zip
	 *		.ZIP
	 */	
	ZipFilenameFilter zipfilter = new ZipFilenameFilter();

	/**
	 * A filter that determines whether a file object is a gzip object
	 * that ends with extension:
	 * 		.gz
	 * 		.GZ
	 * 		.tgz
	 * 		.TGZ
	 */
	GZipFilenameFilter gzipfilter = new GZipFilenameFilter();

	/**
	 * A filter that determines whether a file object is a file
	 */
	FileFilter filefilter = new FileFilter();
	
	/**
	 * DirectoryProcessor Contstructor
	 * @param dir 						- 	The non-empty directory selected by the user
	 * @param sourceDirectoryName 		- 	The name of the directory to copy the files to
	 */
	public DirectoryProcessor(File dir, String sourceDirectoryName){
		this.dir = dir ;
		processDirectory(sourceDirectoryName);
	}
	
	/**
	 * ProcessDirectory method which extracts all files from different types of compressed files.
	 */
	private void processDirectory(String sourceDirectoryName){
		/**
		 * Set the target destination to store a copy of the input directory.
		 * Set this to:
		 * 		$userhome$/Sherlock/$sourceDirectoryName$/Preprocessing/Original
		 *	
		 * 	where $userhome$ is the users home directory
		 * 	and $sourceDirectory$ is the name of the directory selected by the user through the file chooser facility
		 */
		String destination = returnOriginalDirectory(sourceDirectoryName);
							
		if ( ! new File(destination).exists() ) {
			if (new File(destination).mkdirs() ) {
				System.out.println("Success Making " + destination + " Directory");
			} else {
				System.out.println("Failed to create directory!");
			}
		} else {
			System.out.println("Directory Exists");
		}
		
		/**
		 * For each file in the selected directory, copy it to the target destination defined above
		 */
		File[] files = dir.listFiles(filefilter);
		for (File f : files) {
			Path source = f.toPath();
     		Path dest = (new File(destination)).toPath();

			try {
				Files.copy(source, dest.resolve(f.getName()), StandardCopyOption.REPLACE_EXISTING);				// If the file already exists, overwrite it
			} catch ( FileAlreadyExistsException ae ){
				System.out.println("The file already exists in "+ destination);
			} catch (IOException e) {
				System.out.println("Unable to copy File to "+ destination);
				e.printStackTrace();
			} 
		}
		
		File[] zipfiles = dir.listFiles(zipfilter);				// Get all the zip files
		File[] gzipfiles = dir.listFiles(gzipfilter);			// Get all the gzip files
		
		/**
		*  If there are zipped files that need extracting, extract them
		*/
		if ( zipfiles.length > 0 ) {
			System.out.println("Got some zipped files");
			ExtractionContext es_zip = new ExtractionContext(new ZipHandler(), zipfiles, destination);
		}
		/**
		 * If there are gzipped files that need extracting, extract them
		 */
		if ( gzipfiles.length > 0 ) {
			System.out.println("Got some Gzipped files");
			ExtractionContext es_gzip = new ExtractionContext(new GZipHandler(), gzipfiles, destination);
		}
	}
	
	public String returnNewSourceDirectory(String sourceDirectoryName) {
		return getUserHomeDir() + getFileSeparator()
				+ getSherlockDir() + getFileSeparator()
				+ sourceDirectoryName ;
	}
	
	public String returnOriginalDirectory(String sourceDirectoryName) {
		return returnNewSourceDirectory(sourceDirectoryName) + getFileSeparator()
		+ getPreprocessingDir() + getFileSeparator()
		+ getOriginalDir() + getFileSeparator() 
		;
	}
	
	private String getUserHomeDir() {
		return System.getProperty("user.home");
	}
	
	private String getSherlockDir() {
		return "Sherlock" ;
	}
	
	private String getPreprocessingDir() {
		return "Preprocessing" ;
	}
	
	private String getOriginalDir() {
		return "Original" ;
	}
	
	private String getFileSeparator() {
		return File.separator;
	}

}
