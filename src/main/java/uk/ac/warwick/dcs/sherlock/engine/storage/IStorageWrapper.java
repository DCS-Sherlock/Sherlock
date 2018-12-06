package uk.ac.warwick.dcs.sherlock.engine.storage;

public interface IStorageWrapper {

	void close();

	void storeFile(String filename, byte[] fileContent);

}
