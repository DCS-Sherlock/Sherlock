package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import uk.ac.warwick.dcs.sherlock.module.web.models.db.Workspace;

public class WorkspaceWrapper {
    private Workspace local;

    public WorkspaceWrapper() {

    }

    public WorkspaceWrapper(Workspace local) {
        this.local = local;
    }

    public Workspace getLocal() {
        return local;
    }

    public void setLocal(Workspace local) {
        this.local = local;
    }
}
