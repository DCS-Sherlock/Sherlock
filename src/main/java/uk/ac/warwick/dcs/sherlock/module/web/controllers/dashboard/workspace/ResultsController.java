package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.module.web.data.results.SubmissionResultsData;
import uk.ac.warwick.dcs.sherlock.module.web.data.results.JobResultsData;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.data.results.ResultsHelper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.*;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.WorkspaceRepository;

import javax.servlet.http.HttpServletRequest;

/**
 * The controller that deals with the workspace results pages
 */
@Controller
public class ResultsController {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    /**
     * Handles GET requests to the results page
     *
     * @return the path to the results template
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}")
    public String viewGet() {
        return "dashboard/workspaces/results/view";
    }

    /**
     * Handles GET requests to the graph fragment
     *
     * @param isAjax whether or not the request was ajax or not
     * @param pathid the id of the workspace
     * @param jobid the id of the job
     *
     * @return the path to the graph fragment template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @RequestMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/graph")
    public String graphGetFragment(
            @ModelAttribute("isAjax") boolean isAjax,
            @PathVariable(value="pathid") long pathid,
            @PathVariable(value="jobid") long jobid
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/results/" + pathid + "/" + jobid);
        return "dashboard/workspaces/results/fragments/graph";
    }

    /**
     * Handles GET requests to the network graph page
     *
     * @param model holder for model attributes
     * @param request the http request information
     *
     * @return the path to the graph template
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/network")
    public String networkGet(Model model, HttpServletRequest request) {
        if (request.getParameterMap().containsKey("start")) {
            model.addAttribute("start", request.getParameterMap().get("start"));
        } else {
            model.addAttribute("start", "-1");
        }

        return "dashboard/workspaces/results/network";
    }

    /**
     * Handles GET requests to the report submission page
     *
     * @param workspaceWrapper the workspace being managed
     * @param id the id of the submission to report on
     * @param model holder for model attributes
     *
     * @return the path to the report template
     *
     * @throws SubmissionNotFound if the submission was not found
     * @throws MapperException if there was an issue initialising the line mappers
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/report/{submission}")
    public String reportGet(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @PathVariable(value="submission") long id,
            Model model
    ) throws SubmissionNotFound, MapperException {
        ISubmission submission = ResultsHelper.getSubmission(workspaceWrapper, id);

        SubmissionResultsData wrapper = new SubmissionResultsData(submission);

        model.addAttribute("submission", submission);
        model.addAttribute("wrapper", wrapper);
        return "dashboard/workspaces/results/report";
    }

    /**
     * Handles GET requests for the compare submission page
     *
     * @param workspaceWrapper the workspace being managed
     * @param id1 the first submission to compare
     * @param id2 the second submission to compare
     * @param model holder for model attributes
     *
     * @return the path to the compare template
     *
     * @throws SubmissionNotFound if the submission was not found
     * @throws MapperException if there was an issue initialising the line mappers
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/compare/{submission1}/{submission2}")
    public String comparisonGet(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @PathVariable(value="submission1") long id1,
            @PathVariable(value="submission2") long id2,
            Model model
    ) throws SubmissionNotFound, MapperException {
        ISubmission submission1 = ResultsHelper.getSubmission(workspaceWrapper, id1);
        ISubmission submission2 = ResultsHelper.getSubmission(workspaceWrapper, id2);

        SubmissionResultsData wrapper = new SubmissionResultsData(submission1, submission2);

        model.addAttribute("submission1", submission1);
        model.addAttribute("submission2", submission2);
        model.addAttribute("wrapper", wrapper);
        return "dashboard/workspaces/results/compare";
    }

    /**
     * Handles GET requests to the delete results page
     *
     * @return the path to the delete template
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/delete")
    public String deleteGet() {
        return "dashboard/workspaces/results/delete";
    }

    /**
     * Handles GET requests to the delete results page
     *
     * @param workspaceWrapper the workspace being managed
     * @param resultsWrapper the results to delete
     *
     * @return the path to the delete template
     */
    @PostMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/delete")
    public String deletePost(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("results") JobResultsData resultsWrapper
    ) {
        resultsWrapper.getJob().remove();
        return "redirect:/dashboard/workspaces/manage/"+workspaceWrapper.getId()+"?msg=deleted_job";
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
     * Gets the results for the job with the id "jobid"
     *
     * @param workspaceWrapper the workspace being managed
     * @param jobid the job id to find the results
     * @param model holder for model attributes
     *
     * @return the job results wrapper
     *
     * @throws ResultsNotFound if the job was not found in the database
     */
    @ModelAttribute("results")
    private JobResultsData getResults(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @PathVariable(value="jobid") long jobid,
            Model model
    ) throws ResultsNotFound {
        IJob iJob = null;

        for (IJob job : workspaceWrapper.getJobs())
            if (job.getPersistentId() == jobid)
                iJob = job;


        if (iJob == null) throw new ResultsNotFound("Result not found");

        JobResultsData wrapper = new JobResultsData(iJob);
        model.addAttribute("results", wrapper);
        return wrapper;
    }
}