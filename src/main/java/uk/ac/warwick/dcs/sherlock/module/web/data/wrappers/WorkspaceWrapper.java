package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.exception.SubmissionUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Workspace;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.SubmissionsForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.WorkspaceForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.WorkspaceRepository;

import java.io.IOException;
import java.util.*;

public class WorkspaceWrapper {
    private Workspace workspace;
    private IWorkspace iWorkspace;

    public WorkspaceWrapper(
            WorkspaceForm workspaceForm,
            Account account,
            WorkspaceRepository workspaceRepository
    ) {
        this.iWorkspace = SherlockEngine.storage.createWorkspace(workspaceForm.getName(), workspaceForm.getLanguage());
        this.workspace = new Workspace(account, this.iWorkspace.getPersistentId());
        workspaceRepository.save(this.workspace);
    }

    public WorkspaceWrapper(Workspace workspace) throws IWorkspaceNotFound {
        this.init(workspace);
    }

    public WorkspaceWrapper(long id, Account account, WorkspaceRepository workspaceRepository)
            throws WorkspaceNotFound, IWorkspaceNotFound {
        Workspace workspace = workspaceRepository.findByIdAndAccount(id, account);

        if (workspace == null)
            throw new WorkspaceNotFound("Unable to find workspace.");

        this.init(workspace);
    }

    private void init(Workspace workspace) throws IWorkspaceNotFound {
        this.workspace = workspace;

        List<Long> engineId = Collections.singletonList(this.workspace.getEngineId());
        List<IWorkspace> iWorkspaces = SherlockEngine.storage.getWorkspaces(engineId);

        if (iWorkspaces.size() == 1) {
            this.iWorkspace = iWorkspaces.get(0);
        } else {
            throw new IWorkspaceNotFound("Unable to find workspace in engine.");
        }
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public IWorkspace getiWorkspace() {
        return this.iWorkspace;
    }

    public long getId() {
        return this.workspace.getId();
    }

    public long getEngineId() {
        return this.iWorkspace.getPersistentId();
    }

    public String getName() {
        return this.iWorkspace.getName();
    }

    public String getLanguage() {
        return this.iWorkspace.getLanguage();
    }

    public List<ISourceFile> getFiles() {
        return this.iWorkspace.getFiles();
    }

    public List<ISubmission> getSubmissions() {
        return this.iWorkspace.getSubmissions();
    }

    public List<IJob> getJobs() {
        return this.iWorkspace.getJobs();
    }

    public void setName(String name) {
        this.iWorkspace.setName(name);
    }

    public void setLanguage(String language) {
        this.iWorkspace.setLanguage(language);
    }

    public void set(WorkspaceForm workspaceForm) {
        this.setName(workspaceForm.getName());
        this.setLanguage(workspaceForm.getLanguage());
    }

    public void delete(WorkspaceRepository workspaceRepository) {
    	this.iWorkspace.remove();
        workspaceRepository.delete(this.workspace);
    }

    public void addSubmissions(SubmissionsForm submissionsForm, WorkspaceWrapper workspaceWrapper) throws NoFilesUploaded, FileUploadFailed, NotImplementedException {
        int count = 0;

        if(submissionsForm.getFiles().length == 1 && !submissionsForm.getSingle()) {
            throw new NotImplementedException("");
        }

	    for(MultipartFile file : submissionsForm.getFiles()) {
            if (file.getSize() > 0) {
                try {
                	// Each file or archive uploaded creates a new submission. This can be named separately from the file.
	                // Not yet implemented but will support notifying of conflicting submission names, allow to rename or to merge with existing
	                // Each submission can have many files, is populated with the content of archives currently but UI could be extended to allow for individual file uploads to the same submission
	                // 2nd argument is the submission name, should be something unique to easily identify the file or files
	                ISubmission submission = SherlockEngine.storage.createSubmission(workspaceWrapper.getiWorkspace(), FilenameUtils.removeExtension(file.getOriginalFilename()));

                    SherlockEngine.storage.storeFile(submission, file.getOriginalFilename(), file.getBytes());
                } catch (IOException | SubmissionUnsupportedException | WorkspaceUnsupportedException e) {
                    throw new FileUploadFailed(e.getMessage());
                }
                count++;
            }
        }

        if (count == 0) {
            throw new NoFilesUploaded("No submissions uploaded");
        }
    }

    public void runTemplate(TemplateWrapper templateWrapper) throws TemplateContainsNoDetectors, ClassNotFoundException, ParameterNotFound, DetectorNotFound, NoFilesUploaded {
		if (templateWrapper.getTemplate().getDetectors().size() == 0)
		    throw new TemplateContainsNoDetectors("No detectors in chosen template.");

		if (this.getFiles().size() == 0) {
		    throw new NoFilesUploaded("No files in workspace");
        }

		IJob job = this.iWorkspace.createJob();

		for (TDetector td : templateWrapper.getTemplate().getDetectors()) {
            Class<? extends IDetector> detector = (Class<? extends IDetector>) Class.forName(td.getName(), true, SherlockEngine.classloader);
            job.addDetector(detector);
		}

        job.prepare();

		Logger logger = LoggerFactory.getLogger(WorkspaceWrapper.class);
		for (ITask task : job.getTasks()) {
		    for (DetectorWrapper detectorWrapper : templateWrapper.getDetectors()) {
		        if (task.getDetector().getName().equals(detectorWrapper.getEngineDetector().getName())) {
		            for (ParameterWrapper parameterWrapper : detectorWrapper.getParametersList()) {
		                logger.info("Detector "
                                + task.getDetector().getName()
                                + " has had parameter "
                                + parameterWrapper.getParameterObj().getDisplayName()
                                + " set to "
                                + parameterWrapper.getParameter().getValue());
		                task.setParameter(parameterWrapper.getParameterObj(), parameterWrapper.getParameter().getValue());
                    }
                }
            }
        }

		SherlockEngine.executor.submitJob(job);
    }

    public static List<WorkspaceWrapper> findByAccount(Account account, WorkspaceRepository workspaceRepository) {
        List<Workspace> workspaces = workspaceRepository.findByAccount(account);
        List<WorkspaceWrapper> wrappers = new ArrayList<>();

        for (Workspace workspace : workspaces) {
            try {
                wrappers.add(new WorkspaceWrapper(workspace));
            } catch (IWorkspaceNotFound iWorkspaceNotFound) {
                //Workspace no longer found in Engine database, remove from UI databsae
                workspaceRepository.delete(workspace);
            }
        }

        return wrappers;
    }
}
