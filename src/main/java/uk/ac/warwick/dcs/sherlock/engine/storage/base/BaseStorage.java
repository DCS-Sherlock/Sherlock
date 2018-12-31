package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import uk.ac.warwick.dcs.sherlock.api.model.data.AbstractModelProcessedResults;
import uk.ac.warwick.dcs.sherlock.engine.model.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.zip.*;

public class BaseStorage implements IStorageWrapper {

	static BaseStorage instance;

	private EmbeddedDatabase database;
	private BaseStorageFilesystem filesystem;

	public BaseStorage() {
		instance = this;

		this.database = new EmbeddedDatabase();
		this.filesystem = new BaseStorageFilesystem();

		//Do a scan of all files in database in background, check they exist and there are no extra files
		List<EntityFile> orphans = this.filesystem.validateFileStore(this.database.runQuery("SELECT f from File f", EntityFile.class));
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

	@Override
	public IWorkspace createWorkspace() {
		return null;
	}

	@Override
	public Class<? extends AbstractModelProcessedResults> getModelProcessedResultsClass() {
		return null;
	}

	private void storeArchive(String filename, byte[] fileContent) {
		try {
			EntityArchive topArchive = new EntityArchive(filename);
			this.database.storeObject(topArchive);

			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileContent));
			ZipEntry zipEntry = zis.getNextEntry();
			EntityArchive curArchive = topArchive;

			while (zipEntry != null) {
				if (zipEntry.isDirectory()) {
					String[] dirs = FilenameUtils.separatorsToUnix(zipEntry.getName()).split("/");
					curArchive = topArchive;
					for (String dir : dirs) {
						EntityArchive nextArch = curArchive.getChildren() == null ? null : curArchive.getChildren().stream().filter(x -> x.getFilename().equals(dir)).findAny().orElse(null);
						if (nextArch == null) {
							nextArch = new EntityArchive(dir, curArchive);
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

	private void storeIndividualFile(String filename, byte[] fileContent, EntityArchive archive) {
		EntityFile file = new EntityFile(FilenameUtils.getBaseName(filename), FilenameUtils.getExtension(filename), new Timestamp(System.currentTimeMillis()), archive);
		if (!this.filesystem.storeFile(file, fileContent)) {
			return;
		}

		EntityWorkspace workspace = this.database.temporaryWorkspace();
		file.setWorkspace(workspace);

		this.database.storeObject(file);
		//this.filesystem.loadFile(file);
	}
}
