package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.JobStatus;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotAjaxRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The controller that deals with the dashboard homepage
 */
@Controller
public class DashboardController {
	/**
	 * Handles GET requests to the dashboard home
	 *
	 * @return the path to the dashboard page
	 */
	@GetMapping ("/dashboard/index")
	public String index() {
		return "dashboard/index";
	}

    @GetMapping ("/dashboard/index/queue")
    public String queueGetFragment(
            @ModelAttribute("isAjax") boolean isAjax,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/index");

        model = updateModel(model);
        return "dashboard/fragments/queue";
    }

    @PostMapping("/dashboard/index/queue/{id}")
    public String queuePostFragment(
            @ModelAttribute("isAjax") boolean isAjax,
            @PathVariable("id") long id,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/index");

        List<JobStatus> list = SherlockEngine.executor.getAllJobStatuses();
        list = list.stream().filter(j -> j.getId() == id).collect(Collectors.toList());

        if (list.size() == 1) {
            JobStatus jobStatus = list.get(0);

            if (jobStatus.isFinished()) {
                SherlockEngine.executor.dismissJob(jobStatus);
                model.addAttribute("success_msg", "messages.dismissed_job");
            } else {
                SherlockEngine.executor.cancelJob(jobStatus);
                model.addAttribute("success_msg", "messages.cancelled_job");
            }
        }

        model = updateModel(model);
        return "dashboard/fragments/queue";
    }

	@GetMapping ("/dashboard/index/statistics")
	public String statisticsGetFragment(
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/index");

		List<IWorkspace> workspaces = SherlockEngine.storage.getWorkspaces();
        model.addAttribute("workspaces", workspaces.size());
        model.addAttribute("submissions", workspaces.stream().mapToInt(w -> w.getSubmissions().size()).sum());

        return "dashboard/fragments/statistics";
	}

	private Model updateModel(Model model) {
        model.addAttribute(
                "jobs",
                SherlockEngine.executor.getAllJobStatuses()
        );

        model.addAttribute("executor", SherlockEngine.executor);

        return model;
    }
}