package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultJob;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.component.WorkStatus;
import uk.ac.warwick.dcs.sherlock.engine.exception.ResultJobUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.exception.SubmissionUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.report.ReportManager;
import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;

import javax.persistence.Query;
import java.util.*;
import java.util.stream.*;

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
	public void mergePendingSubmission(ISubmission existing, ISubmission pending) throws SubmissionUnsupportedException {
		if (!(existing instanceof EntityArchive) || !(pending instanceof EntityArchive)) {
			throw new SubmissionUnsupportedException("ISubmission instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}

		EntityArchive s1 = (EntityArchive) existing;
		EntityArchive s2 = (EntityArchive) pending;

		if (s1.pendingWorkspace != null || s1.getWorkspace() == null) {
			throw new SubmissionUnsupportedException("Existing is pending, it must already exist in the database");
		}

		this.mergeChildSubmissions(s1, s2);
	}

	@Override
	public void removePendingSubmission(ISubmission pendingSubmission) throws SubmissionUnsupportedException {
		if (!(pendingSubmission instanceof EntityArchive)) {
			throw new SubmissionUnsupportedException("ISubmission instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}
		EntityArchive s = (EntityArchive) pendingSubmission;

		s.remove();
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
	public List<ITuple<ISubmission, ISubmission>> storeFile(IWorkspace workspace, String filename, byte[] fileContent) throws WorkspaceUnsupportedException {
		return this.storeFile(workspace, filename, fileContent, false);
	}

	@Override
	public List<ITuple<ISubmission, ISubmission>> storeFile(IWorkspace workspace, String filename, byte[] fileContent, boolean archiveContainsMultipleSubmissions)
			throws WorkspaceUnsupportedException {
		List<ITuple<ISubmission, ISubmission>> collisions = FileUploadHelper.storeFile(this.database, this.filesystem, workspace, filename, fileContent, archiveContainsMultipleSubmissions);

		return collisions;
	}

	@Override
	public void writePendingSubmission(ISubmission pendingSubmission) throws SubmissionUnsupportedException {
		if (!(pendingSubmission instanceof EntityArchive)) {
			throw new SubmissionUnsupportedException("ISubmission instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}
		EntityArchive s = (EntityArchive) pendingSubmission;

		s.writeToPendingWorkspace();
	}

	ISubmission createPendingSubmission(IWorkspace workspace, String submissionName) throws WorkspaceUnsupportedException {
		if (!(workspace instanceof EntityWorkspace)) {
			throw new WorkspaceUnsupportedException("IWorkspace instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}
		EntityWorkspace w = (EntityWorkspace) workspace;

		return new EntityArchive(w, submissionName);
	}

	ISubmission createSubmission(IWorkspace workspace, String submissionName) throws WorkspaceUnsupportedException {
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

	void removeCodeBlockGroups() {
		Query q = BaseStorage.instance.database.createQuery("DELETE FROM CodeBlockGroup e WHERE e.type = \"---remove---\"");
		BaseStorage.instance.database.executeUpdate(q);

		q = BaseStorage.instance.database.createQuery("DELETE FROM CodeBlock b WHERE b.size = -5");
		BaseStorage.instance.database.executeUpdate(q);
	}

	private void mergeChildSubmissions(EntityArchive s1, EntityArchive s2) {
		s2.getChildren_().forEach(c2 -> {
			EntityArchive c1 = s1.getChildren().stream().filter(x -> x.getName().equals(c2.getName())).findAny().orElse(null);
			if (c1 != null) {
				mergeChildSubmissions(c1, c2);
			}
			else {
				c2.setParent(s1);
			}
		});

		s2.getFiles_().forEach(f -> {
			f.setArchive(s1);
			this.database.storeObject(f);
		});

		this.database.storeObject(s1);

		try {
			BaseStorage.instance.database.refreshObject(s2);
			BaseStorage.instance.database.removeObject(s2);
		}
		catch (Exception e) {
		}
	}
}
