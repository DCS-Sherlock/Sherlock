package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.SubmissionsForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.WorkspaceForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.TemplateWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TemplateRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

import javax.validation.Valid;
import java.util.List;

@Controller
public class ManageWorkspaceController {
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private TemplateRepository templateRepository;

    public ManageWorkspaceController() { }

	@GetMapping("/dashboard/workspaces/manage/{pathid}")
	public String manageGet() {
		return "dashboard/workspaces/manage";
	}

    @GetMapping("/dashboard/workspaces/manage/details/{pathid}")
    public String nameGetFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("isAjax") boolean isAjax,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

        model.addAttribute("workspaceForm", new WorkspaceForm(workspaceWrapper));
        model.addAttribute("languageList", SherlockRegistry.getLanguages());
        return "dashboard/workspaces/fragments/details";
    }

    @PostMapping("/dashboard/workspaces/manage/details/{pathid}")
    public String namePostFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("isAjax") boolean isAjax,
			@Valid @ModelAttribute WorkspaceForm workspaceForm,
			BindingResult result,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

		if (!result.hasErrors()) {
		    workspaceWrapper.set(workspaceForm);
            model.addAttribute("success", true);
		}

        model.addAttribute("languageList", SherlockRegistry.getLanguages());
        return "dashboard/workspaces/fragments/details";
    }

    @GetMapping("/dashboard/workspaces/manage/submissions/{pathid}")
    public String submissionsGetFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("isAjax") boolean isAjax,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

        model.addAttribute("submissionsForm", new SubmissionsForm());
        return "dashboard/workspaces/fragments/submissions";
    }

    @PostMapping("/dashboard/workspaces/manage/submissions/{pathid}")
    public String submissionsPostFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("isAjax") boolean isAjax,
            @Valid @ModelAttribute SubmissionsForm submissionsForm,
            BindingResult result,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

        if (!result.hasErrors()) {
            workspaceWrapper.addSubmissions(submissionsForm, workspaceWrapper);
            model.addAttribute("success", true);
        }

        return "dashboard/workspaces/fragments/submissions";
    }

    @GetMapping("/dashboard/workspaces/manage/jobs/{pathid}")
    public String jobsGetFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("account") Account account,
            @ModelAttribute("isAjax") boolean isAjax,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

        model.addAttribute("templates", TemplateWrapper.findByAccountAndPublicAndLanguage(account, templateRepository, workspaceWrapper.getLanguage()));
        return "dashboard/workspaces/fragments/jobs";
    }

    @PostMapping("/dashboard/workspaces/manage/jobs/{pathid}")
    public String jobsPostFragment(
            @PathVariable("pathid") long pathid,
            @RequestParam(value="template_id", required=true) long template_id,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("account") Account account,
            @ModelAttribute("isAjax") boolean isAjax,
            Model model
    ) throws NotAjaxRequest, TemplateNotFound {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

        TemplateWrapper templateWrapper = new TemplateWrapper(template_id, account, templateRepository);

        try {
            workspaceWrapper.runTemplate(templateWrapper);
            model.addAttribute("success_msg", "workspaces_analysis_started");
        } catch (TemplateContainsNoDetectors e) {
            model.addAttribute("warning_msg", "workspaces_analysis_no_detectors");
        } catch (ClassNotFoundException e) {
            model.addAttribute("warning_msg", "workspaces_analysis_detector_missing");
        } catch (ParameterNotFound e) {
            model.addAttribute("warning_msg", "workspaces_analysis_parameter_missing");
        }

        model.addAttribute("templates", TemplateWrapper.findByAccountAndPublic(account, templateRepository));
        return "dashboard/workspaces/fragments/jobs";
    }

    @GetMapping("/dashboard/workspaces/manage/results/{pathid}")
    public String resultsGetFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("isAjax") boolean isAjax,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

		List<IJob> jobs = workspaceWrapper.getiWorkspace().getJobs();
		model.addAttribute("jobs", jobs);

        return "dashboard/workspaces/fragments/results";
    }

    @GetMapping("/dashboard/workspaces/delete/{pathid}")
    public String deleteGet() {
        return "dashboard/workspaces/delete";
    }

    @PostMapping("/dashboard/workspaces/delete/{pathid}")
    public String deletePost(@ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper) {
        workspaceWrapper.delete(workspaceRepository);
		return "redirect:/dashboard/workspaces?msg=deleted";
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
}