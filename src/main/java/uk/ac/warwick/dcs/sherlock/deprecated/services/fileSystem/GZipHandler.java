package uk.ac.warwick.dcs.sherlock.deprecated.services.fileSystem;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;

/**
 * @author Aliyah Handles the extraction of files from GZip formats.
 */
class GZipHandler implements ExtractionStrategy {

	/**
	 * For each GZip file, extract its contents
	 *
	 * @param dir         - The file to be extracted
	 * @param destination - The destination the files are to extracted to
	 */
	@Override
	public void extract(File[] dir, String destination) {
		System.out.println("Extraction Strategy: \t GZipHandler");
		for (File f : dir) {
			System.out.println("The filename \t" + f);
			unzip(f, destination);
		}
	}

	/**
	 * The extraction tokenise of the GZip handler.
	 *
	 * @param f           - The GZip file to unzip
	 * @param destination - The destination the extracted files are to be copied to
	 */
	private void unzip(File f, String destination) {
		try {
			/** Create a file input stream to read the source file **/
			FileInputStream fis = new FileInputStream(f);

			/** Create a buffered input stream to read the input stream **/
			BufferedInputStream buffis = new BufferedInputStream(fis);

			/** Create a GZip input stream to read the buffer	stream **/
			GzipCompressorInputStream gzis = new GzipCompressorInputStream(buffis);

			/** Create a Tar input stream to read the GZip input stream	**/
			TarArchiveInputStream tis = new TarArchiveInputStream(gzis);

			TarArchiveEntry tarEntry = null;

			/** While there are tarEntries to be extracted **/
			while ((tarEntry = (TarArchiveEntry) tis.getNextEntry()) != null) {
				System.out.println("Extracting: " + tarEntry.getName());

				/** If the entry is a directory, skip **/
				if (tarEntry.isDirectory()) {
					continue;
				}
				else {
					int count;
					byte data[] = new byte[1024];

					//System.out.println("Output Stream " + destination + File.separator + tarEntry.getName());

					FileOutputStream fos = new FileOutputStream(destination + File.separator + tarEntry.getName());
					BufferedOutputStream dest = new BufferedOutputStream(fos, 1024);
					while ((count = tis.read(data, 0, 1024)) != -1) {
						dest.write(data, 0, count);
					}
					// Close the Output Stream
					dest.close();
				}
			}
			// Close all input streams
			fis.close();
			buffis.close();
			gzis.close();
			tis.close();
			System.out.println("untar completed successfully!!");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
