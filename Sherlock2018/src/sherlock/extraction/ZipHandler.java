package sherlock.extraction;

import java.io.*;
import java.util.zip.*;


public class ZipHandler implements ExtractionStrategy {

	@Override
	public void extract(File[] dir, String destination) {
		System.out.println("Extraction Strategy: \t ZipHandler");
		for ( File f : dir ) {										// For each file in the zip directory
			System.out.println("The filename \t" + f);
			try {
				unzip( f, destination);								// Unzip the file
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Extracts each zip file and stores the result in the users filing system
	 * @param zipFile
	 * @throws IOException
	 */
	private static void unzip(File zipFile, String destination) throws IOException{
		System.out.println("Unzipping using the ZipHandler");

		FileInputStream is ;

		// Buffer for reading and writing data to file
		byte[] buffer = new byte[1024];

		try {
			is = new FileInputStream(zipFile);
			ZipInputStream zip_is = new ZipInputStream(is);
			ZipEntry ze = zip_is.getNextEntry();

			// While there are more entries in the zip file
			while ( ze != null ){
				String filename = ze.getName();
				System.out.println("Zip filename: " + filename);

				if (!ze.isDirectory()) {	// If the entry is a file
					int slashIndex = filename.lastIndexOf(File.separator);
					System.out.println("File Separator index: " + slashIndex);
					System.out.println("Entry name length " + filename.length());
					
					filename = filename.substring(slashIndex+1);	

					if (!filename.startsWith("_") && !filename.startsWith(".")) {							// If the entry is not a hidden file
						File file = new File(destination + File.separator + filename);
						System.out.println("Unzipping to "+ file.getAbsolutePath());

						FileOutputStream fos = new FileOutputStream(file);
						int len;
						while ((len = zip_is.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.close();
					} else {
						System.out.println("Entry is a hidden file: \t" + ze.toString() + "\n"); 
						ze = zip_is.getNextEntry();	
						continue;
					}	
				} else {
					System.out.println("Entry is a directory: \t" + ze.toString() + "\n");
				}
				ze = zip_is.getNextEntry();				
			}
			zip_is.closeEntry();
        	zip_is.close();
		} catch (IOException e ){
			System.out.println("Unable to unzip the file");
			e.printStackTrace();
		}
	}
}	
