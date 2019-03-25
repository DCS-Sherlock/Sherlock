package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;
import uk.ac.warwick.dcs.sherlock.engine.component.*;
import uk.ac.warwick.dcs.sherlock.engine.exception.ResultJobUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.exception.SubmissionUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.report.ReportManager;
import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;

import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.*;
import java.util.zip.*;

public class BaseStorage implements IStorageWrapper {

	static BaseStorage instance;
	static Logger logger = LoggerFactory.getLogger(BaseStorage.class);

	EmbeddedDatabase database;
	BaseStorageFilesystem filesystem;

	private Map<Long, ReportManager> reportManagerCache;
	private ArrayDeque<Long> reportManagerCacheQueue;

	public BaseStorage() {
		instance = this;

		this.database = new EmbeddedDatabase();
		this.filesystem = new BaseStorageFilesystem();

		int cacheCapacity = 3;
		this.reportManagerCache = new HashMap<>();
		this.reportManagerCacheQueue = new ArrayDeque<>(cacheCapacity);
		for (int i = 0; i < cacheCapacity; i++) {
			this.reportManagerCacheQueue.add((long) -1);
		}

		//Do a scan of all files in database in background, check they exist and there are no extra files
		List orphans = this.filesystem.validateFileStore(this.database.runQuery("SELECT f from File f", EntityFile.class), this.database.runQuery("SELECT t from Task t", EntityTask.class));
		if (orphans != null && orphans.size() > 0) {
			this.database.removeObject(orphans);
		}

		//list = this.database.runQuery("SELECT t from Task t", EntityTask.class).stream().filter(x -> x.getStatus() == WorkStatus.PREPARED).collect(Collectors.toList());
		List<EntityJob> jobs = this.database.runQuery("SELECT j from Job j", EntityJob.class);
		jobs.stream().filter(j -> j.getTasks().size() > 0 && j.getStatus() == WorkStatus.ACTIVE).forEach(j -> {
			if (j.getTasks().stream().anyMatch(i -> i.getStatus() == WorkStatus.PREPARED)) {
				j.getTasks().stream().filter(i -> i.getStatus() == WorkStatus.PREPARED).forEach(i -> ((EntityTask) i).setStatus(WorkStatus.INTERRUPTED));
				j.setStatus(WorkStatus.INTERRUPTED);
			}
		});

		List<Long> fids = this.database.runQuery("SELECT f from File f", EntityFile.class).stream().map(EntityFile::getPersistentId).collect(Collectors.toList());
		jobs.stream().filter(j -> !fids.containsAll(j.getFilesList())).forEach(j -> j.setStatus(WorkStatus.MISSING_FILES));

		jobs = jobs.stream().filter(j -> j.getTasks().size() == 0).collect(Collectors.toList());
		if (jobs.size() > 0) {
			logger.warn("Removing jobs with no tasks...");
			jobs.forEach(EntityJob::remove);
		}

		this.removeCodeBlockGroups();
	}

	@Override
	public void close() {
		this.database.close();
	}

	@Override
	public ISubmission createSubmission(IWorkspace workspace, String submissionName) throws WorkspaceUnsupportedException {
		if (!(workspace instanceof EntityWorkspace)) {
			throw new WorkspaceUnsupportedException("IWorkspace instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}
		EntityWorkspace w = (EntityWorkspace) workspace;

		if (w.getSubmissions().stream().anyMatch(x -> x.getName().equals(submissionName))) {
			logger.info("Duplicate submission name: " + submissionName);
			return null;
		}

		EntityArchive submission = new EntityArchive(submissionName);
		submission.setSubmissionArchive(w);
		this.database.storeObject(submission);
		this.database.refreshObject(w);

		return submission;
	}

	@Override
	public IWorkspace createWorkspace(String name, String lang) {
		IWorkspace w = new EntityWorkspace(name, lang);
		this.database.storeObject(w);
		return w;
	}

	@Override
	public Class<? extends ICodeBlockGroup> getCodeBlockGroupClass() {
		return EntityCodeBlockGroup.class;
	}

	@Override
	public ReportManager getReportGenerator(IResultJob resultJob) throws ResultJobUnsupportedException {
		if (!(resultJob instanceof EntityResultJob)) {
			throw new ResultJobUnsupportedException("IResultJob instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}
		EntityResultJob res = (EntityResultJob) resultJob;

		if (this.reportManagerCache.containsKey(res.getPersistentId())) {
			if (this.reportManagerCacheQueue.remove(res.getPersistentId())) {
				this.reportManagerCacheQueue.add(res.getPersistentId());
			}

			return this.reportManagerCache.get(res.getPersistentId());
		}
		else {
			long rem = this.reportManagerCacheQueue.remove();
			if (rem > -1) {
				this.reportManagerCache.remove(rem);
			}

			ReportManager manager = new ReportManager(res);

			this.reportManagerCache.put(res.getPersistentId(), manager);
			this.reportManagerCacheQueue.add(res.getPersistentId());

			return manager;
		}
	}

	@Override
	public ISourceFile getSourceFile(long persistentId) {
		List<EntityFile> f = this.database.runQuery("SELECT f FROM File f WHERE f.id=" + persistentId, EntityFile.class);
		if (f.size() != 1) {
			logger.warn("File of id {} does not exist", persistentId);
		}
		return f.get(0);
	}

	@Override
	public ISubmission getSubmissionFromName(IWorkspace workspace, String submissionName) throws WorkspaceUnsupportedException {
		if (!(workspace instanceof EntityWorkspace)) {
			throw new WorkspaceUnsupportedException("IWorkspace instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}
		EntityWorkspace w = (EntityWorkspace) workspace;

		return w.getSubmissions().stream().filter(x -> x.getName().equals(submissionName)).findAny().orElse(null);
	}

	@Override
	public List<IWorkspace> getWorkspaces(List<Long> ids) {
		return this.getWorkspaces().stream().filter(x -> ids.contains(x.getPersistentId())).collect(Collectors.toList());
	}

	@Override
	public List<IWorkspace> getWorkspaces() {
		List<EntityWorkspace> l = this.database.runQuery("SELECT w FROM Workspace w", EntityWorkspace.class);
		return new LinkedList<>(l);
	}

	@Override
	public boolean storeCodeBlockGroups(List<ICodeBlockGroup> groups) {
		List<Object> objects = new LinkedList<>();
		for (ICodeBlockGroup group : groups) {
			if (group instanceof EntityCodeBlockGroup) {
				EntityCodeBlockGroup g = (EntityCodeBlockGroup) group;
				objects.addAll(g.blockMap.values());
				objects.add(g);
			}
		}

		if (objects.size() > 0) {
			this.database.storeObject(objects);
			return true;
		}

		return false;
	}

	@Override
	@Deprecated
	public void storeFile(IWorkspace workspace, String filename, byte[] fileContent) throws WorkspaceUnsupportedException, SubmissionUnsupportedException {
		EntityArchive sub = (EntityArchive) this.createSubmission(workspace, FilenameUtils.removeExtension(filename));
		if (sub != null) {
			this.storeFile(sub, filename, fileContent);
		}
	}

	@Override
	public void storeFile(ISubmission submission, String filename, byte[] fileContent) throws SubmissionUnsupportedException {
		if (!(submission instanceof EntityArchive)) {
			throw new SubmissionUnsupportedException("ISubmission instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}
		EntityArchive s = (EntityArchive) submission;

		//Set all the jobs as outdated
		for (IJob job : ((EntityArchive) submission).getWorkspace().getJobs()) {
			job.setStatus(WorkStatus.OUTDATED);
		}

		if (FilenameUtils.getExtension(filename).equals("zip")) {
			this.storeArchive(s, fileContent);
		}
		else {
			this.storeIndividualFile(s, filename, fileContent);
		}

		this.database.refreshObject(s);
	}

	void removeCodeBlockGroups() {
		Query q = BaseStorage.instance.database.createQuery("DELETE FROM CodeBlockGroup e WHERE e.type = \"---remove---\"");
		BaseStorage.instance.database.executeUpdate(q);

		q = BaseStorage.instance.database.createQuery("DELETE FROM CodeBlock b WHERE b.size = -5");
		BaseStorage.instance.database.executeUpdate(q);
	}

	private void storeArchive(EntityArchive submission, byte[] fileContent) {
		try {
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileContent));
			ZipEntry zipEntry = zis.getNextEntry();
			EntityArchive curArchive = submission;

			while (zipEntry != null) {
				if (zipEntry.isDirectory()) {
					String[] dirs = FilenameUtils.separatorsToUnix(zipEntry.getName()).split("/");
					curArchive = submission;
					for (String dir : dirs) {
						EntityArchive nextArchive = curArchive.getChildren() == null ? null : curArchive.getChildren().stream().filter(x -> x.getName().equals(dir)).findAny().orElse(null);

						if (nextArchive == null) {
							nextArchive = new EntityArchive(dir, curArchive);
							this.database.storeObject(nextArchive);
						}
						curArchive = nextArchive;
					}
				}
				else {
					this.storeIndividualFile(curArchive, zipEntry.getName(), IOUtils.toByteArray(zis));
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

	private void storeIndividualFile(EntityArchive archive, String filename, byte[] fileContent) {
		int line = 0;
		int contentLine = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileContent)));

		try {
			while (reader.ready()) {
				line++;
				if (!reader.readLine().equals("")) {
					contentLine++;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		EntityFile file =
				new EntityFile(archive, FilenameUtils.getBaseName(filename), FilenameUtils.getExtension(filename), new Timestamp(System.currentTimeMillis()), fileContent.length, line, contentLine);
		if (!this.filesystem.storeFile(file, fileContent)) {
			return;
		}

		this.database.storeObject(file);
	}
}
