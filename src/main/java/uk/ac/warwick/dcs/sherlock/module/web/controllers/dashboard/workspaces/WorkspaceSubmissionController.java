package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

@Controller
public class WorkspaceSubmissionController {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    public WorkspaceSubmissionController() { }

    @GetMapping("/dashboard/workspaces/manage/{pathid}/submission/{submissionid}")
    public String viewGet() {
        return "dashboard/workspaces/submissions/view";
    }

    @GetMapping(value = "/dashboard/workspaces/manage/{pathid}/submission/{submissionid}/file/{fileid}/{filename}", produces = "text/plain")
    @ResponseBody
    public String fileGet(
            @ModelAttribute("submission") ISubmission submission,
            @PathVariable(value="fileid") long fileid
    ) {
        ISourceFile sourceFile = null;

        for (ISourceFile temp : submission.getContainedFiles()) {
            if (temp.getPersistentId() == fileid) {
                sourceFile = temp;
            }
        }

        if (sourceFile == null) {
            return "404: FILE NOT FOUND";
        }

        return sourceFile.getFileContentsAsString();
    }

    @GetMapping("/dashboard/workspaces/manage/{pathid}/submission/{submissionid}/delete")
    public String deleteGet() {
        return "dashboard/workspaces/submissions/delete";
    }

    @PostMapping("/dashboard/workspaces/manage/{pathid}/submission/{submissionid}/delete")
    public String deletePost(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("submission") ISubmission submission
    ) {
        //TODO: actually delete the submission
	    submission.remove();
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
    private ISubmission getSubmission(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @PathVariable(value="submissionid") long submissionid,
            Model model) throws SubmissionNotFound {
        ISubmission submission = null;

        for (ISubmission temp : workspaceWrapper.getSubmissions()) {
            if (temp.getId() == submissionid) {
                submission = temp;
            }
        }

        if (submission == null) {
            throw new SubmissionNotFound("Submission not found");
        }

        model.addAttribute("submission", submission);
        return submission;
    }
}