package uk.ac.warwick.dcs.sherlock.deprecated.services.fileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.*;

/**
 * @author Aliyah Handles the extraction of files from Zip formats.
 */
class ZipHandler implements ExtractionStrategy {

	/**
	 * For each GZip file, extract its contents
	 *
	 * @param dir         - The file to be extracted
	 * @param destination - The destination the files are to extracted to
	 */
	@Override
	public void extract(File[] dir, String destination) {
		System.out.println("Extraction Strategy: \t ZipHandler");
		for (File f : dir) {                                        // For each file in the zip directory
			System.out.println("The filename \t" + f);
			unzip(f, destination);                                // Unzip the file
		}
	}

	/**
	 * Extracts each zip file and stores the result in the users filing system
	 *
	 * @param zipFile
	 *
	 * @throws IOException
	 */
	private static void unzip(File zipFile, String destination) {
		System.out.println("Unzipping using the ZipHandler");

		FileInputStream is;

		// Buffer for reading and writing data to file
		byte[] buffer = new byte[1024];

		try {
			/** Create a file input stream to read the source file **/
			is = new FileInputStream(zipFile);

			/** Create a zip input stream to read the input stream **/
			ZipInputStream zip_is = new ZipInputStream(is);

			ZipEntry ze = zip_is.getNextEntry();

			/** While there are more entries in the zip file **/
			while ((ze = (ZipEntry) zip_is.getNextEntry()) != null) {
				String filename = ze.getName();
				System.out.println("Zip filename: " + filename);

				if (!ze.isDirectory()) {                                                            // If the entry is a file
					int slashIndex = filename.lastIndexOf(File.separator);
					System.out.println("File Separator index: " + slashIndex);
					System.out.println("Entry name length " + filename.length());

					filename = filename.substring(slashIndex + 1);

					if (!filename.startsWith("_") && !filename.startsWith(".")) {                    // If the entry is not a hidden file
						File dest = new File(destination + File.separator + filename);
						System.out.println("Unzipping to " + dest.getAbsolutePath());

						FileOutputStream fos = new FileOutputStream(dest);
						int len;
						while ((len = zip_is.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.close();
					}
					else {
						System.out.println("Entry is a hidden file: \t" + ze.toString() + "\n");
						continue;
					}
				}
				else {
					System.out.println("Entry is a directory: \t" + ze.toString() + "\n");
				}
			}
			zip_is.closeEntry();
			zip_is.close();
		}
		catch (IOException e) {
			System.out.println("Unable to unzip the file");
			e.printStackTrace();
		}
	}
}	
