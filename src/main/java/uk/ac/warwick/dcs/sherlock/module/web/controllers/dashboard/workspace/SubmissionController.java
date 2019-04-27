package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.data.results.ResultsHelper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.WorkspaceRepository;

/**
 * The controller that deals with the workspace submission pages
 */
@Controller
public class SubmissionController {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    /**
     * Handles GET requests to the view submission page
     *
     * @return the path to the view template
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/submission/{submissionid}")
    public String viewGet() {
        return "dashboard/workspaces/submissions/view";
    }

    /**
     * Handles GET requests to the view file page
     *
     * @param submission the submission being managed
     * @param fileid the id of the file
     *
     * @return the plaintext contents of the file
     *
     * @throws SourceFileNotFound if the file was not found
     */
    @GetMapping(value = "/dashboard/workspaces/manage/{pathid}/submission/{submissionid}/file/{fileid}/{filename}", produces = "text/plain")
    @ResponseBody
    public String fileGet(
            @ModelAttribute("submission") ISubmission submission,
            @PathVariable(value="fileid") long fileid,
            @ModelAttribute("isPrinting") boolean isPrinting
    ) throws SourceFileNotFound {
        ISourceFile sourceFile = this.getFile(submission, fileid);

        if (isPrinting) {
            String result = "";
            int i = 1;
            for (String line : sourceFile.getFileContentsAsStringList()) {
                result += i + " | " + line + System.getProperty("line.separator");
                i++;
            }
            return result;
        }

        return sourceFile.getFileContentsAsString();
    }

    /**
     * Handles GET requests to the delete submission page
     *
     * @return the path to the delete template
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/submission/{submissionid}/delete")
    public String deleteGet() {
        return "dashboard/workspaces/submissions/delete";
    }

    /**
     * Handles POST requests to the delete submission page
     *
     * @param workspaceWrapper the workspace being managed
     * @param submission the submission being managed
     *
     * @return a direct to the workspace page
     */
    @PostMapping("/dashboard/workspaces/manage/{pathid}/submission/{submissionid}/delete")
    public String deletePost(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("submission") ISubmission submission
    ) {
	    submission.remove();
        return "redirect:/dashboard/workspaces/manage/"+workspaceWrapper.getId()+"?msg=deleted_submission";
    }

    /**
     * Handles GET requests to the delete file page
     *
     * @param submission the submission being managed
     * @param fileid the id of the file to delete
     * @param model  holder for model attributes
     *
     * @return the path to the delete file template
     *
     * @throws SourceFileNotFound if the file doesn't exist
     */
    @GetMapping(value = "/dashboard/workspaces/manage/{pathid}/submission/{submissionid}/file/{fileid}/{filename}/delete")
    public String deleteFileGet(
            @ModelAttribute("submission") ISubmission submission,
            @PathVariable(value="fileid") long fileid,
            Model model
    ) throws SourceFileNotFound {
        ISourceFile sourceFile = this.getFile(submission, fileid);
        model.addAttribute("file", sourceFile);
        return "dashboard/workspaces/submissions/deleteFile";
    }

    /**
     * Handles POST requests to the delete file page
     *
     * @param workspaceWrapper the workspace being managed
     * @param submission the submission being managed
     * @param fileid the id of the file to delete
     *
     * @return a direct to the workspace page
     *
     * @throws SourceFileNotFound if the file doesn't exist
     */
    @PostMapping(value = "/dashboard/workspaces/manage/{pathid}/submission/{submissionid}/file/{fileid}/{filename}/delete")
    public String deleteFilePost(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("submission") ISubmission submission,
            @PathVariable(value="fileid") long fileid
    ) throws SourceFileNotFound {
        ISourceFile sourceFile = this.getFile(submission, fileid);
        sourceFile.remove();
        return "redirect:/dashboard/workspaces/manage/"+workspaceWrapper.getId()+"/submission/"+submission.getId()+"?msg=deleted_file";
    }

    /**
     * Gets the workspace where the id equals the "pathid" path variable
     *
     * @param account the account of the current user
     * @param pathid the workspace id
     * @param model holder for model attributes
     *
     * @return the workspace wrapper
     *
     * @throws IWorkspaceNotFound if the workspace was not found in the Engine database
     * @throws WorkspaceNotFound if the workspace was not found in the web database
     */
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

    /**
     * Gets the submission where the id equals the "submissionid" path variable
     *
     * @param workspaceWrapper the workspace being managed
     * @param submissionid the id of the submission to find
     * @param model holder for model attributes
     *
     * @return the submission wrapper
     *
     * @throws SubmissionNotFound if the submission was not found
     */
    @ModelAttribute("submission")
    private ISubmission getSubmission(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @PathVariable(value="submissionid") long submissionid,
            Model model) throws SubmissionNotFound {
        ISubmission submission = ResultsHelper.getSubmission(workspaceWrapper, submissionid);
        model.addAttribute("submission", submission);
        return submission;
    }

    /**
     * Gets the file where the id equals the "fileid" variable
     *
     * @param submission the submission to find the file in
     * @param fileid the id of the file to delete
     *
     * @return the file
     *
     * @throws SourceFileNotFound if the file doesn't exist
     */
    private ISourceFile getFile(ISubmission submission, long fileid) throws SourceFileNotFound {
        ISourceFile sourceFile = null;

        for (ISourceFile temp : submission.getAllFiles()) {
            if (temp.getPersistentId() == fileid) {
                sourceFile = temp;
            }
        }

        if (sourceFile == null) {
            throw new SourceFileNotFound("File not found");
        }

        return sourceFile;
    }
}