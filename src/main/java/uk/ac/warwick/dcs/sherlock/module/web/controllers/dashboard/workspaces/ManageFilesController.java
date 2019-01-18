package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.SubmissionsForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.WorkspaceForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.TemplateWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TemplateRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class ManageFilesController {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    public ManageFilesController() { }

    @GetMapping("/dashboard/workspaces/manage/submission/{pathid}/{fileid}")
    public String fileGet() {
        return "dashboard/workspaces/files/view";
    }

    @GetMapping("/dashboard/workspaces/manage/submission/{pathid}/{fileid}/delete")
    public String deleteGet() {
        return "dashboard/workspaces/files/delete";
    }

    @PostMapping("/dashboard/workspaces/manage/submission/{pathid}/{fileid}/delete")
    public String deletePost(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("submission") ISourceFile sourceFile
    ) {
        //TODO: actually delete the submission
        return "redirect:/dashboard/workspaces/manage/"+workspaceWrapper.getId()+"?msg=deleted_submission";
    }

    @ModelAttribute("workspace")
    public WorkspaceWrapper getWorkspaceWrapper(
            @ModelAttribute("account") Account account,
            @PathVariable(value="pathid") long pathid,
            Model model)
            throws IWorkspaceNotFound, WorkspaceNotFound
    {
        WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(pathid, account, workspaceRepository);
        model.addAttribute("workspace", workspaceWrapper);
        return workspaceWrapper;
    }

    @ModelAttribute("submission")
    public ISourceFile getSourceFile(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @PathVariable(value="fileid") long fileid,
            Model model) throws SourceFileNotFound {
        ISourceFile sourceFile = null;

        for (ISourceFile file : workspaceWrapper.getFiles()) {
            if (file.getPersistentId() == fileid) {
                sourceFile = file;
            }
        }

        if (sourceFile == null) throw new SourceFileNotFound("File not found.");

        model.addAttribute("submission", sourceFile);
        return sourceFile;
    }
}