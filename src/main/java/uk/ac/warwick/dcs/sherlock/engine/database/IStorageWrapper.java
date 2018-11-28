package uk.ac.warwick.dcs.sherlock.engine.database;

import org.springframework.web.multipart.MultipartFile;

public interface IStorageWrapper {

	void storeFile(MultipartFile file);

}
