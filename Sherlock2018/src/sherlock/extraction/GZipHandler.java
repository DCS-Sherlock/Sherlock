package sherlock.extraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

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
		String filename = f.getAbsolutePath();
	    System.out.println(filename );

	    //remove .gz in filename
	    int dotindex = filename.lastIndexOf('.');
	    String tarname = filename.substring(0, dotindex);
	   
	    //remove .tar in filename
	    dotindex = tarname.lastIndexOf('.');
	    filename = tarname.substring(0, dotindex);
	    
	   try {
		   // Create a file input stream to read the source file
		   FileInputStream fis = new FileInputStream(f);
		   
		   // Create a gzip input stream to extract the source file
           GZIPInputStream gzis = new GZIPInputStream(fis);
		   
           // Create a buffer
           byte[] buffer = new byte[1024];
           int length;
           
           // Define the location to store the extracted files
           File dest = new File(destination + File.separator + filename);
           System.out.println("Unzipping to "+ dest.getAbsolutePath());
           
           // Create file output stream where the extracted result is to be stored
           FileOutputStream fos = new FileOutputStream(dest);

           // Extract the GZip file to the destination 
           while ((length = gzis.read(buffer)) > 0) {
               fos.write(buffer, 0, length);
           }
           
           // Close all the input and output streams
           fos.close();
           gzis.close();
           fis.close();
	   } catch ( IOException e ) {
		   e.printStackTrace();
	   }
	}

}
