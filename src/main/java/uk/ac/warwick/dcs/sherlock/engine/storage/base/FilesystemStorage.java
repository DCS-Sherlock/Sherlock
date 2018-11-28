package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

import java.io.File;
import java.io.IOException;

public class FilesystemStorage{

	/**
	 * Stores a file on the filesystem
	 * @param fileidentifier
	 * @param fileContent
	 * @param key
	 * @return hash of the stored file, for verification
	 */
	public String storeFile(String fileidentifier, byte[] fileContent, String key) {
		String hash = DigestUtils.sha512Hex(fileContent);
		System.out.println(fileidentifier);
		System.out.println(fileidentifier.substring(0, 2));
		System.out.println(fileidentifier.substring(2,4));

		String dirpath = SherlockEngine.configuration.getDataPath() + File.separator + "Store" + File.separator + fileidentifier.substring(0, 2) + File.separator + fileidentifier.substring(2,4);
		try {
			FileUtils.writeByteArrayToFile(new File(dirpath + File.separator + fileidentifier), fileContent);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return hash;
	}

	/**
	 * Loads a file from the filesystem
	 * @param filename
	 * @param hash
	 * @param key
	 * @return
	 */
	public byte[] loadFile(String filename, String hash, String key) {
		return new byte[0];
	}
}
