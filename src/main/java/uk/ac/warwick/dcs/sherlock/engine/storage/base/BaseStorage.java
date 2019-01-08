package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelProcessedResult;
import uk.ac.warwick.dcs.sherlock.engine.model.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.zip.*;

public class BaseStorage implements IStorageWrapper {

	static BaseStorage instance;
	private static Logger logger = LoggerFactory.getLogger(BaseStorage.class);

	EmbeddedDatabase database;
	BaseStorageFilesystem filesystem;

	public BaseStorage() {
		instance = this;

		this.database = new EmbeddedDatabase();
		this.filesystem = new BaseStorageFilesystem();

		//Do a scan of all files in database in background, check they exist and there are no extra files
		List<Object> orphans = this.filesystem.validateFileStore(this.database.runQuery("SELECT f from File f", EntityFile.class), this.database.runQuery("SELECT t from Task t", EntityTask.class));
		if (orphans != null && orphans.size() > 0) {
			this.database.removeObject(orphans);
		}
		this.database.runQuery("SELECT j from Job j", EntityJob.class).stream().filter(j -> j.getTasks().size() == 0).peek(j -> this.database.removeObject(j)).findAny().ifPresent(x -> logger.warn("removing jobs with no tasks"));

		//List<EntityTask> tasks = this.database.runQuery("SELECT t from Task t", EntityTask.class);
		//logger.warn(tasks.get(0).getRawResults().stream().map(Objects::toString).collect(Collectors.joining("\n----\n")));
	}

	@Override
	public void close() {
		this.database.close();
	}

	@Override
	public IWorkspace createWorkspace() {
		return this.database.temporaryWorkspace();
	}

	@Override
	public Class<? extends IModelProcessedResult> getModelProcessedResultsClass() {
		return null;
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
	}
}
