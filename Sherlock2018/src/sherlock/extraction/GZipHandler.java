package sherlock.extraction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

class GZipHandler implements ExtractionStrategy {

	@Override
	public void extract(File[] dir, String destination) {
		System.out.println("Extraction Strategy: \t GZipHandler");
		for ( File f : dir ) {
			System.out.println("The filename \t" + f);
			unzip(f, destination);
		}

	}

	private void unzip(File f, String destination)  {
		try {
			// Create a file input stream to read the source file
			FileInputStream fis = new FileInputStream(f);
			
			BufferedInputStream buffis = new BufferedInputStream(fis);
			GzipCompressorInputStream gzis = new GzipCompressorInputStream(buffis);
			TarArchiveInputStream tis = new TarArchiveInputStream(gzis);
			
			TarArchiveEntry tarEntry = null;
			
			while ((tarEntry = (TarArchiveEntry) tis.getNextEntry()) != null) {
				   System.out.println("Extracting: " + tarEntry.getName());
				   /** If the entry is a directory, create the directory. **/
				   if (tarEntry.isDirectory()) {
//					   System.out.println("A directory" + tarEntry.getName());
//					   File file = new File(destination + File.pathSeparator + tarEntry.getName());
//				      
//					   file.mkdirs();
					   continue;
				   } else { 
					   int count;
					   byte data[] = new byte[1024];
					   System.out.println("Output Stream " + destination + File.separator + tarEntry.getName());
					   FileOutputStream fos = new FileOutputStream(destination + File.separator + tarEntry.getName() );
					   BufferedOutputStream dest = new BufferedOutputStream(fos, 1024);
					   while ((count = tis.read(data, 0, 1024)) != -1) {
						   dest.write(data, 0, count);
					   }
					   dest.close();
				   }
			}
			tis.close();
			System.out.println("untar completed successfully!!");

		} catch (IOException e ) {
			e.printStackTrace();
		}
	}
	
	
//	private void unzip(File f, String destination)  {
//		String filename = f.getAbsolutePath();
//	    System.out.println(filename );
//
//	    //remove .gz in filename
//	    int dotindex = filename.lastIndexOf('.');
//	    String tarname = filename.substring(0, dotindex);
//	   
//	    //remove .tar in filename
//	    dotindex = tarname.lastIndexOf('.');
//	    filename = tarname.substring(0, dotindex);
//	    
//	    int slashIndex = filename.lastIndexOf(File.separator);
//		System.out.println("File Separator index: " + slashIndex);
//		System.out.println("Entry name length " + filename.length());
//		String name = filename.substring(slashIndex+1);
//		
//	   try {
//		   // Create a file input stream to read the source file
//		   FileInputStream fis = new FileInputStream(f);
//		   
//		   // Create a gzip input stream to extract the source file
//           GZIPInputStream gzis = new GZIPInputStream(fis);
//           
//		   
//           // Create a buffer
//           byte[] buffer = new byte[1024];
//           int length;
//           
//           // Define the location to store the extracted files
//           File dest = new File(destination + File.separator + name + ".txt");
//           System.out.println("Unzipping to "+ dest.getAbsolutePath());
//           
//           // Create file output stream where the extracted result is to be stored
//           FileOutputStream fos = new FileOutputStream(dest);
//
//           // Extract the GZip file to the destination 
//           while ((length = gzis.read(buffer)) > 0) {
//               fos.write(buffer, 0, length);
//           }
//           
//           // Close all the input and output streams
//           fos.close();
//           gzis.close();
//           fis.close();
//	   } catch ( IOException e ) {
//		   e.printStackTrace();
//	   }
//	}

}
