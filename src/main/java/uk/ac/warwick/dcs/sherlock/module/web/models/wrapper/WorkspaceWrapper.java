package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.model.IWorkspace;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Workspace;

import java.util.*;

public class WorkspaceWrapper {
    private Workspace workspace;
    private IWorkspace iWorkspace;

    public WorkspaceWrapper(Workspace workspace) {
        this.workspace = workspace;
        List<Long> id = Collections.singletonList(this.workspace.getEngineId());
        List<IWorkspace> iWorkspaces = SherlockEngine.storage.getWorkspaces(id);
        if (iWorkspaces.size() == 1) {
            this.iWorkspace = iWorkspaces.get(0);
        } else {
            this.createEngineWorkspace();
        }
    }

    public WorkspaceWrapper(String name, Account account) {
        this.workspace = new Workspace(name, account);
        this.createEngineWorkspace();
    }

    private void createEngineWorkspace() {
        this.iWorkspace = SherlockEngine.storage.createWorkspace(this.workspace.getName(), Language.JAVA); // change the construction method so we do one db write, not three
        this.workspace.setEngineId(this.iWorkspace.getPersistentId());
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public IWorkspace getiWorkspace() {
        return this.iWorkspace;
    }

    public void setiWorkspace(IWorkspace iWorkspace) {
        this.iWorkspace = iWorkspace;
    }
}
