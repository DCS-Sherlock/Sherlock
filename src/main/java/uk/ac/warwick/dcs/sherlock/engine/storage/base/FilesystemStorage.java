package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

public class FilesystemStorage{

	public String storeFile(String filename, InputStream fileContent, String key) {
		try {
			DigestUtils.sha1(fileContent);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] loadFile(String filename, String hash, String key) {
		return new byte[0];
	}
}
