package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Workspace;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.SubmissionsForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.WorkspaceForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.WorkspaceRepository;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;

import java.io.IOException;
import java.util.*;

/**
 * The wrapper that manages the workspaces
 */
public class WorkspaceWrapper {
    /**
     * The database workspace entity
     */
    private Workspace workspace;
    /**
     * The engine workspace entity
     */
    private IWorkspace iWorkspace;

    /**
     * Initialise the workspace wrapper using the workspace form to create
     * a new workspace
     *
     * @param workspaceForm the form
     * @param account the account of the current user
     * @param workspaceRepository the database repository
     */
    public WorkspaceWrapper(
            WorkspaceForm workspaceForm,
            Account account,
            WorkspaceRepository workspaceRepository
    ) {
        this.iWorkspace = SherlockEngine.storage.createWorkspace(workspaceForm.getName(), workspaceForm.getLanguage());
        this.workspace = new Workspace(account, this.iWorkspace.getPersistentId());
        workspaceRepository.save(this.workspace);
    }

    /**
     * Initialise the workspace wrapper using an existing workspace
     *
     * @param workspace the workspace to manage
     *
     * @throws IWorkspaceNotFound if the workspace entity was not found in the engine
     */
    public WorkspaceWrapper(Workspace workspace) throws IWorkspaceNotFound {
        this.init(workspace);
    }

    /**
     * Initialise the workspace wrapper using an id to find one in the database
     *
     * @param id the id of the account to find
     * @param account the account of the current user
     * @param workspaceRepository the database repository
     *
     * @throws WorkspaceNotFound if the workspace was not found in the web database
     * @throws IWorkspaceNotFound if the workspace entity was not found in the engine
     */
    public WorkspaceWrapper(long id, Account account, WorkspaceRepository workspaceRepository)
            throws WorkspaceNotFound, IWorkspaceNotFound {
        Workspace workspace = workspaceRepository.findByIdAndAccount(id, account);

        if (workspace == null)
            throw new WorkspaceNotFound("Unable to find workspace.");

        this.init(workspace);
    }

    /**
     * Finish initialising the wrapper
     *
     * @param workspace the workspace ot initialise the wrapper with
     *
     * @throws IWorkspaceNotFound if the workspace entity was not found in the engine
     */
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

    /**
     * Get the web database workspace entity
     *
     * @return the workspace
     */
    public Workspace getWorkspace() {
        return workspace;
    }

    /**
     * Get the engine workspace entity
     *
     * @return the workspace
     */
    public IWorkspace getiWorkspace() {
        return this.iWorkspace;
    }

    /**
     * Get the workspace web id
     *
     * @return the id
     */
    public long getId() {
        return this.workspace.getId();
    }

    /**
     * Get the workspace engine id
     *
     * @return the id
     */
    public long getEngineId() {
        return this.iWorkspace.getPersistentId();
    }

    /**
     * Get the workspace name
     *
     * @return the name
     */
    public String getName() {
        return this.iWorkspace.getName();
    }

    /**
     * Get the workspace language
     *
     * @return the language
     */
    public String getLanguage() {
        return this.iWorkspace.getLanguage();
    }

    /**
     * Get the workspace files
     *
     * @return the list of files
     */
    public List<ISourceFile> getFiles() {
        return this.iWorkspace.getFiles();
    }

    /**
     * Get the workspace submissions
     *
     * @return the list of submissions
     */
    public List<ISubmission> getSubmissions() {
        return this.iWorkspace.getSubmissions();
    }

    /**
     * Get the workspace jobs
     *
     * @return the list of jobs
     */
    public List<IJob> getJobs() {
        return this.iWorkspace.getJobs();
    }

    /**
     * Set the workspace name
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.iWorkspace.setName(name);
    }

    /**
     * Set the workspace language
     *
     * @param language the new language
     */
    public void setLanguage(String language) {
        this.iWorkspace.setLanguage(language);
    }

    /**
     * Update the workspace using the workspace form
     *
     * @param workspaceForm the form to use
     */
    public void set(WorkspaceForm workspaceForm) {
        this.setName(workspaceForm.getName());
        this.setLanguage(workspaceForm.getLanguage());
    }

    /**
     * Delete the workspace
     *
     * @param workspaceRepository the database repository
     */
    public void delete(WorkspaceRepository workspaceRepository) {
    	this.iWorkspace.remove();
        workspaceRepository.delete(this.workspace);
    }

    /**
     * Add submissions to this workspace
     *
     * @param submissionsForm the form to use
     *
     * @throws NoFilesUploaded if no files were uploaded
     * @throws FileUploadFailed if uploading the files failed
     * @throws NotImplementedException if they attempt to upload multiple submissions in a single zip
     */
    public void addSubmissions(SubmissionsForm submissionsForm) throws NoFilesUploaded, FileUploadFailed, NotImplementedException, DuplicateSubmissionNames {
        int count = 0; //the number of submissions uploaded
        int duplicates = 0; //the number of submissions that already exists

        if(submissionsForm.getFiles().length == 1 && !submissionsForm.getSingle()) {
            throw new NotImplementedException("");
        }

	    for(MultipartFile file : submissionsForm.getFiles()) {
            if (file.getSize() > 0) {
                try {
                	SherlockEngine.storage.storeFile(this.getiWorkspace(), file.getOriginalFilename(), file.getBytes());
                } catch (IOException | WorkspaceUnsupportedException e) {
                    throw new FileUploadFailed(e.getMessage());
                }
                count++;
            }
        }

        if (count == 0) {
            throw new NoFilesUploaded("No submissions uploaded");
        }

        if (duplicates > 0) {
            throw new DuplicateSubmissionNames("Duplicates: " + duplicates);
        }
    }

    /**
     * Runs a template on a workspace
     *
     * @param templateWrapper the template to run
     *
     * @throws TemplateContainsNoDetectors if there are no detectors in the template
     * @throws ClassNotFoundException if the detector no longer exists
     * @throws ParameterNotFound if the parameter no longer exists
     * @throws DetectorNotFound if the detector no longer exists
     * @throws NoFilesUploaded if no files were uploaded
     */
    public long runTemplate(TemplateWrapper templateWrapper) throws TemplateContainsNoDetectors, ClassNotFoundException, ParameterNotFound, DetectorNotFound, NoFilesUploaded {
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

		return job.getPersistentId();
    }

    /**
     * Gets the list of workspaces owned by the current user
     *
     * @param account the account of the current user
     * @param workspaceRepository the database repository
     *
     * @return the list of workspaces
     */
    public static List<WorkspaceWrapper> findByAccount(Account account, WorkspaceRepository workspaceRepository) {
        List<Workspace> workspaces = workspaceRepository.findByAccount(account);
        List<WorkspaceWrapper> wrappers = new ArrayList<>();

        for (Workspace workspace : workspaces) {
            try {
                wrappers.add(new WorkspaceWrapper(workspace));
            } catch (IWorkspaceNotFound iWorkspaceNotFound) {
                //Workspace no longer found in Engine database, remove from UI database
                workspaceRepository.delete(workspace);
            }
        }

        return wrappers;
    }
}
