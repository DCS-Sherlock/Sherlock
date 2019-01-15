package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.springframework.stereotype.Component;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.IWorkspaceNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.WorkspaceNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Workspace;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

import java.util.*;

public class WorkspaceWrapper {
    private Workspace workspace;
    private IWorkspace iWorkspace;

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

    public WorkspaceWrapper(String name, Language language, Account account, WorkspaceRepository workspaceRepository) {
        this.iWorkspace = SherlockEngine.storage.createWorkspace(name, language);
        this.workspace = new Workspace(account, this.iWorkspace.getPersistentId());
        workspaceRepository.save(this.workspace);
    }

    private void init(Workspace workspace) throws IWorkspaceNotFound {
        this.workspace = workspace;

        List<Long> id = Collections.singletonList(this.workspace.getEngineId());
        List<IWorkspace> iWorkspaces = SherlockEngine.storage.getWorkspaces(id);

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

    public Language getLanguage() {
        return this.iWorkspace.getLanguage();
    }

    public List<ISourceFile> getFiles() {
        return this.iWorkspace.getFiles();
    }

    public List<IJob> getJobs() {
        return this.iWorkspace.getJobs();
    }

    public void setName(String name) {
        this.iWorkspace.setName(name);
    }

    public void setLanguage(Language language) {
        this.iWorkspace.setLanguage(language);
    }

    public static List<WorkspaceWrapper> getWorkspacesByAccount(Account account, WorkspaceRepository workspaceRepository) {
        List<Workspace> workspaces = workspaceRepository.findByAccount(account);
        List<WorkspaceWrapper> wrappers = new ArrayList<>();

        for (Workspace workspace : workspaces) {
            try {
                wrappers.add(new WorkspaceWrapper(workspace));
            } catch (IWorkspaceNotFound iWorkspaceNotFound) {
                //TODO: log error
            }
        }

        return wrappers;
    }
}
