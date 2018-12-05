package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.entities.DBArchive;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.entities.DBFile;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.entities.DBStudent;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.entities.DBWorkspace;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.zip.*;

public class BaseStorage implements IStorageWrapper {

	private EmbeddedDatabase database;
	private FilesystemStorage filesystem;

	public BaseStorage() {
		this.database = new EmbeddedDatabase();
		this.filesystem = new FilesystemStorage();

		//Do a scan of all files in database in background, check they exist and there are no extra files
		List<DBFile> orphans = this.filesystem.validateFileStore(this.database.runQuery("SELECT f from File f", DBFile.class));
		if (orphans != null && orphans.size() > 0) {
			this.database.removeObject(orphans);
		}
	}

	@Override
	public void close() {
		this.database.close();
	}

	@Override
	public void storeFile(String filename, byte[] fileContent) {
		if (FilenameUtils.getExtension(filename).equals("zip")) {
			this.storeArchive(filename, fileContent);
		}
		else {
			this.storeIndividualFile(filename, fileContent, null);
		}
	}

	private void storeIndividualFile(String filename, byte[] fileContent, DBArchive archive) {
		DBFile file = new DBFile(FilenameUtils.getBaseName(filename), FilenameUtils.getExtension(filename), new Timestamp(System.currentTimeMillis()), archive);
		if (!this.filesystem.storeFile(file, fileContent)) {
			return;
		}

		DBStudent student = this.database.temporaryStudent();
		DBWorkspace workspace = this.database.temporaryWorkspace();
		file.setStudent(student);
		file.setWorkspace(workspace);

		this.database.storeObject(file);
		//this.filesystem.loadFile(file);
	}

	private void storeArchive(String filename, byte[] fileContent) {
		try {
			DBArchive topArchive = new DBArchive(filename);
			this.database.storeObject(topArchive);

			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileContent));
			ZipEntry zipEntry = zis.getNextEntry();
			DBArchive curArchive = topArchive;

			while (zipEntry != null) {
				if (zipEntry.isDirectory()) {
					String[] dirs = FilenameUtils.separatorsToUnix(zipEntry.getName()).split("/");
					curArchive = topArchive;
					for (String dir : dirs) {
						DBArchive nextArch = curArchive.getChildren() == null ? null : curArchive.getChildren().stream().filter(x -> x.getFilename().equals(dir)).findAny().orElse(null);
						if (nextArch == null) {
							nextArch = new DBArchive(dir, curArchive);
							curArchive.addChild(nextArch);
							this.database.storeObject(nextArch);
						}
						curArchive = nextArch;
					}
				}
				else {
					this.storeIndividualFile(zipEntry.getName(), IOUtils.toByteArray(zis), curArchive);
				}
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
