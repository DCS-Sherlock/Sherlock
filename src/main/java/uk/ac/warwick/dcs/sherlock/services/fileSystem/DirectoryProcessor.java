package uk.ac.warwick.dcs.sherlock.services.fileSystem;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;

import uk.ac.warwick.dcs.sherlock.services.fileSystem.ExtractionContext;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.AcceptedFileFilter;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.DirectoryFilter;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.GZipFilenameFilter;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.ZipFilenameFilter;

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
	
	/**
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
	AcceptedFileFilter filefilter = new AcceptedFileFilter();
	
	private File[] inputFiles;
	
	
	/**
	 * DirectoryProcessor Constructor 	- 	Used when a new session is being created and files may need decompressing
	 * @param dir 						- 	The non-empty directory selected by the user
	 * @param sourceDirectoryName 		- 	The name of the directory to copy the files to
	 */
	public DirectoryProcessor(File dir, String sourceDirectoryName){
		this.dir = dir ;
		extractFiles(sourceDirectoryName);
	}
	
	/**
	 * DirectoryProcessor Constructor 	- 	Used to filter the files by some file filter 
	 * @param dir 						- 	The non-empty directory to be filtered
	 * @param f							- 	The filter to be used
	 */
	public DirectoryProcessor( File dir, FileFilter f ) {
		this.dir = dir;
		this.inputFiles = getInputFiles(dir, f);
	}
	
	/**
	 * DirectoryProcessor Constructor 	- 	Used to filter the files by some filename filter 
	 * @param dir 						- 	The non-empty directory to be filtered
	 * @param f							- 	The filename filter to be used
	 */
	public DirectoryProcessor( File dir, FilenameFilter f ) {
		this.dir = dir;
		this.inputFiles = getInputFiles(dir, f);
	}
	
	/**
	 * ProcessDirectory method which extracts all files from different types of compressed files.
	 */
	private void extractFiles(String sourceDirectoryName){
		/**
		 * Set the target destination to store a copy of the input directory.
		 * Set this to:
		 * 		$userhome$/Sherlock/$sourceDirectoryName$/Preprocessing/Original
		 *	
		 * 	where $userhome$ is the users home directory
		 * 	and $sourceDirectory$ is the name of the directory selected by the user through the file chooser facility
		 */
		String destination = returnOriginalDirectory(sourceDirectoryName);
		System.out.println(destination);					
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
	
	/**
	 * Return all of the files that are to be detected over as a result of the applied filter.
	 * This method is called by the DirectoryProcessory constructor which is called in the 
	 * Preprocessor class prior to pre-processing
	 * @param dir	- The Preprocessing/Original directory containing all the extracted files that are to be detected over
	 * @param f 		- The file filter to apply to the 'dir' parameter
	 * @return 		- An array of files that are consistent with the file filter
	 */
	private File[] getInputFiles(File dir, FileFilter f) {
		return dir.listFiles(f);
	}
	
	/**
	 * Return all of the files that adhere to the filtering option.
	 * @param dir	- The Preprocessing/Original directory to filter
	 * @param f 		- The filename filter to apply to the 'dir' parameter
	 * @return 		- An array of files that are consistent with the filename filter
	 */
	private File[] getInputFiles(File dir, FilenameFilter f) {
		return dir.listFiles(f);
	}
	
	/**
	 * Return all of the files that are to be detected over
	 * @return 		- File paths to be detected over
	 */
	public File[] getInputFiles() {
		return inputFiles;
	}

	public String returnNewSourceDirectory(String sourceDirectoryName) {
		return getUserHomeDir() + getFileSeparator()
				+ getSherlockDir() + getFileSeparator()
				+ sourceDirectoryName ;
	}
	
	public String returnOriginalDirectory(String sourceDirectoryName) {
		return returnNewSourceDirectory(sourceDirectoryName) + getFileSeparator()
		+ getPreprocessingDirName() + getFileSeparator()
		+ getOriginalDir() + getFileSeparator() 
		;
	}
	
	private String getUserHomeDir() {
		return System.getProperty("user.home");
	}
	
	private String getSherlockDir() {
		return "Sherlock" ;
	}
	
	private String getPreprocessingDirName() {
		return "Preprocessing" ;
	}
	
	private String getOriginalDir() {
		return "Original" ;
	}
	
	private String getFileSeparator() {
		return File.separator;
	}

}
