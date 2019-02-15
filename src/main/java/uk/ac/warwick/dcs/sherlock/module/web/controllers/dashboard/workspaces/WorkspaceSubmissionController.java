package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

@Controller
public class WorkspaceSubmissionController {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    public WorkspaceSubmissionController() { }

    @GetMapping("/dashboard/workspaces/manage/submission/{pathid}/{fileid}")
    public String fileGet() {
        return "dashboard/workspaces/files/view";
    }

    @GetMapping(value = "/dashboard/workspaces/manage/submission/{pathid}/{fileid}/{filename}", produces = "text/plain")
    @ResponseBody
    public String filePlainGet(
            @ModelAttribute("submission") ISourceFile file
    ) {
        return file.getFileContentsAsString();
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
    private WorkspaceWrapper getWorkspaceWrapper(
            @ModelAttribute("account") AccountWrapper account,
            @PathVariable(value="pathid") long pathid,
            Model model)
            throws IWorkspaceNotFound, WorkspaceNotFound
    {
        WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(pathid, account.getAccount(), workspaceRepository);
        model.addAttribute("workspace", workspaceWrapper);
        return workspaceWrapper;
    }

    @ModelAttribute("submission")
    private ISourceFile getSourceFile(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @PathVariable(value="fileid") long fileid,
            Model model) throws SourceFileNotFound {
        ISourceFile sourceFile = null;

        for (ISourceFile file : workspaceWrapper.getFiles())
            if (file.getPersistentId() == fileid)
                sourceFile = file;

        if (sourceFile == null) throw new SourceFileNotFound("File not found.");

        model.addAttribute("submission", sourceFile);
        return sourceFile;
    }
}