package uk.ac.warwick.dcs.sherlock.engine.database;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FilesystemStorage implements IStorageWrapper {

	@Override
	public void storeFile(MultipartFile file) {
		try {
			file.getInputStream();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
