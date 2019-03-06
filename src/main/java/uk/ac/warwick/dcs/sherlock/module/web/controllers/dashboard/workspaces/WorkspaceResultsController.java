package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspaces;

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

@Controller
public class WorkspaceResultsController {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    public WorkspaceResultsController() { }

    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}")
    public String viewGet() {
        return "dashboard/workspaces/results/view";
    }

    @RequestMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/graph")
    public String graphGetFragment(
            @ModelAttribute("isAjax") boolean isAjax,
            @PathVariable(value="pathid") long pathid,
            @PathVariable(value="jobid") long jobid
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/results/" + pathid + "/" + jobid);
        return "dashboard/workspaces/results/fragments/graph";
    }

    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/network")
    public String networkGet( ) {
        return "dashboard/workspaces/results/network";
    }

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

    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/delete")
    public String deleteGet() {
        return "dashboard/workspaces/results/delete";
    }

    @PostMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/delete")
    public String deletePost(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("results") JobResultsData resultsWrapper
    ) {
        //TODO: actually delete the job
        return "redirect:/dashboard/workspaces/manage/"+workspaceWrapper.getId()+"?msg=deleted_job";
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

//    private
}