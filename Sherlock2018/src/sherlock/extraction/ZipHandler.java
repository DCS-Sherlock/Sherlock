package sherlock.extraction;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

public class ZipHandler implements ExtractionStrategy {

	@Override
	public void extract(File[] dir) {
		System.out.println("Extraction Strategy: \t ZipHandler");
		for ( File f : dir ) {										// For each file in the zip directory
			System.out.println("The filename \t" + f);
			try {
				unzip(new ZipFile(f));								// Unzip the file
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
	private static void unzip(ZipFile zipFile) throws IOException{
		System.out.println("Extraction Strategy: \t ZipHandler\t Unzipping");
	}
	
}
