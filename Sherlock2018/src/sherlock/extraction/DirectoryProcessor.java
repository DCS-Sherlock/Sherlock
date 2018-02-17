/**
 * 
 */
package sherlock.extraction;

import java.io.File;
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
	}
	
	/**
	 * ProcessDirectory method which extracts all files from different types of compressed files.
	 */
	public Collection<File> processDirectory(){
		File[] files = dir.listFiles(filefilter);
		
		System.out.println(files.toString());
		File[] zipfiles = dir.listFiles(zipfilter);		// Get all the zip files
//		File[] gzipfiles = dir.listFiles(gzipfilter);	// Get all the gzip files
		
		
		Collection<File> zip = Arrays.asList(zipfiles) ;
//		Collection<File> gzip = Arrays.asList(zipfiles) ;
		
		if ( zipfiles.length > 0 ) {
			System.out.println("Got some zipped files");
			
			ExtractionContext es_zip = new ExtractionContext(new ZipHandler(), zipfiles);
		}
//		if ( gzipfiles.length > 0 ) {
//			ExtractionContext es_gzip = new ExtractionContext(new GZipHandler(), gzipfiles);
//		}
		System.out.println(System.getProperty("user.home"));
//		Store all of the files in the pre-processing directory as originals
		
		return Arrays.asList(files);
	}
	
	
}
