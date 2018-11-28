package uk.ac.warwick.dcs.sherlock.engine.database;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface IStorageWrapper {

	String storeFile(String filename, InputStream fileContent, String key);

	byte[] loadFile(String filename, String hash, String key);

}
