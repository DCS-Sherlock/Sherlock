package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.IWorkspaceNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.JobNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.WorkspaceNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.JobWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

@Controller
public class ManageJobController {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    public ManageJobController() { }

    @GetMapping("/dashboard/workspaces/manage/results/{pathid}/{jobid}")
    public String viewGet() {
        return "dashboard/workspaces/results/view";
    }

    @GetMapping("/dashboard/workspaces/manage/results/{pathid}/{jobid}/delete")
    public String deleteGet() {
        return "dashboard/workspaces/results/delete";
    }

    @PostMapping("/dashboard/workspaces/manage/results/{pathid}/{jobid}/delete")
    public String deletePost(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("job") JobWrapper jobWrapper
    ) {
        //TODO: actually delete the job
        return "redirect:/dashboard/workspaces/manage/"+workspaceWrapper.getId()+"?msg=deleted_job";
    }

    @ModelAttribute("workspace")
    public WorkspaceWrapper getWorkspaceWrapper(
            @ModelAttribute("account") AccountWrapper account,
            @PathVariable(value="pathid") long pathid,
            Model model)
            throws IWorkspaceNotFound, WorkspaceNotFound
    {
        WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(pathid, account.getAccount(), workspaceRepository);
        model.addAttribute("workspace", workspaceWrapper);
        return workspaceWrapper;
    }

    @ModelAttribute("job")
    public JobWrapper getJob(
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @PathVariable(value="jobid") long jobid,
            Model model) throws JobNotFound {
        IJob iJob = null;

        for (IJob job : workspaceWrapper.getJobs())
            if (job.getPersistentId() == jobid)
                iJob = job;


        if (iJob == null) throw new JobNotFound("Job not found");

        JobWrapper wrapper = new JobWrapper(iJob);
        model.addAttribute("job", wrapper);
        return wrapper;
    }
}