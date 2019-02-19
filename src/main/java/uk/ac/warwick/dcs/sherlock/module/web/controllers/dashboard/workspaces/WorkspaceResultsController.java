package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.ResultsWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

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

    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/report/{file1}")
    public String reportGet() {
        return "dashboard/workspaces/results/report";
    }

    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/compare/{file1}/{file2}")
    public String comparisonGet() {
        return "dashboard/workspaces/results/compare";
    }

    @GetMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/delete")
    public String deleteGet() {
        return "dashboard/workspaces/results/delete";
    }

    @PostMapping("/dashboard/workspaces/manage/{pathid}/results/{jobid}/delete")
    public String deletePost(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("results") ResultsWrapper resultsWrapper
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
    private ResultsWrapper getResults(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @PathVariable(value="jobid") long jobid,
            Model model
    ) throws ResultsNotFound {
        IJob iJob = null;

        for (IJob job : workspaceWrapper.getJobs())
            if (job.getPersistentId() == jobid)
                iJob = job;


        if (iJob == null) throw new ResultsNotFound("Result not found");

        ResultsWrapper wrapper = new ResultsWrapper(iJob);
        model.addAttribute("results", wrapper);
        return wrapper;
    }

//    private
}