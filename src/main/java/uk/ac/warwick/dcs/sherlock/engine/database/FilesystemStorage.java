package uk.ac.warwick.dcs.sherlock.engine.database;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

public class FilesystemStorage implements IStorageWrapper {

	@Override
	public String storeFile(String filename, InputStream fileContent, String key) {
		try {
			DigestUtils.sha1(fileContent);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] loadFile(String filename, String hash, String key) {
		return new byte[0];
	}
}
