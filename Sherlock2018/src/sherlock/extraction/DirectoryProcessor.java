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
//	GZipFilenameFilter gzipfilter = new GZipFilenameFilter();
	FileFilter filefilter = new FileFilter();
	
	public DirectoryProcessor(File dir){
		this.dir = dir ;
		processDirectory();
	}
	
	/**
	 * ProcessDirectory method which extracts all files from different types of compressed files.
	 */
	private void processDirectory(){
		String destination = System.getProperty("user.home") + File.separator + "Sherlock";
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
//		File[] gzipfiles = dir.listFiles(gzipfilter);	// Get all the gzip files
		
		
		Collection<File> zip = Arrays.asList(zipfiles) ;
//		Collection<File> gzip = Arrays.asList(zipfiles) ;
		
		if ( zipfiles.length > 0 ) {
			System.out.println("Got some zipped files");
			
			ExtractionContext es_zip = new ExtractionContext(new ZipHandler(), zipfiles, destination);
		}
//		if ( gzipfiles.length > 0 ) {
//			ExtractionContext es_gzip = new ExtractionContext(new GZipHandler(), gzipfiles);
//		}
		System.out.println(System.getProperty("user.home"));
//		Store all of the files in the pre-processing directory as originals
		
	}
	
	
}
