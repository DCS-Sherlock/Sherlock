package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.SubmissionsForm;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.WorkspaceForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.TemplateWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TemplateRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.WorkspaceRepository;

import javax.validation.Valid;
import java.util.List;

/**
 * The controller that deals with the manage workspace pages
 */
@Controller
public class WorkspaceController {
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private TemplateRepository templateRepository;

    /**
     * Handles the GET request to the manage workspace page
     *
     * @return the path to the manage template
     */
	@GetMapping("/dashboard/workspaces/manage/{pathid}")
	public String manageGet() {
		return "dashboard/workspaces/manage";
	}

    /**
     * Handles the GET request to the workspace details fragment
     *
     * @param pathid the id of the workspace
     * @param workspaceWrapper the workspace being managed
     * @param isAjax whether or not the request was ajax or not
     * @param model holder for model attributes
     *
     * @return the path to the workspace details fragment
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/details")
    public String detailsGetFragment(
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

    /**
     * Handles POST requests to the workspace details fragment
     *
     * @param pathid the id of the workspace
     * @param workspaceWrapper the workspace being managed
     * @param isAjax whether or not the request was ajax or not
     * @param workspaceForm the form that should be submitted in the request
     * @param result the results of the validation on the form above
     * @param model holder for model attributes
     *
     * @return the path to the details fragment template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @PostMapping("/dashboard/workspaces/manage/{pathid}/details")
    public String detailsPostFragment(
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
            model.addAttribute("success_msg", "workspaces.details.updated");
		}

        model.addAttribute("languageList", SherlockRegistry.getLanguages());
        return "dashboard/workspaces/fragments/details";
    }

    /**
     * Handles GET requests to the upload submissions page
     *
     * @return the path to the upload template
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/submissions/upload")
    public String uploadGetFragment() {
        return "dashboard/workspaces/submissions/upload";
    }

    /**
     * Handles POST requests to the upload submissions page
     *
     * @param pathid the id of the workspace
     * @param workspaceWrapper the workspace being managed
     * @param isAjax whether or not the request was ajax or not
     * @param submissionsForm the form that should be submitted in the request
     * @param result the results of the validation on the form above
     * @param model holder for model attributes
     *
     * @return the path to the upload confirmed template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @PostMapping("/dashboard/workspaces/manage/{pathid}/submissions/upload")
    public String uploadPostFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("isAjax") boolean isAjax,
            @Valid @ModelAttribute SubmissionsForm submissionsForm,
            BindingResult result,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

        if (!result.hasErrors()) {
            try {
                workspaceWrapper.addSubmissions(submissionsForm, workspaceWrapper);
                model.addAttribute("success_msg", "workspaces.submissions.uploaded");
            } catch (NoFilesUploaded e) {
                result.reject("error.file.empty");
            } catch (FileUploadFailed e) {
                result.reject("error.file.failed");
            } catch (NotImplementedException e) {
                result.reject("error.not_implemented");
            }
        }

        return "dashboard/workspaces/submissions/uploadConfirm";
    }

    /**
     * Handles the GET requests for the submissions list fragment
     *
     * @param pathid the id of the workspace
     * @param isAjax whether or not the request was ajax or not
     *
     * @return the path to the submissions list template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/submissions")
    public String submissionsGetFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("isAjax") boolean isAjax
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

        return "dashboard/workspaces/fragments/submissions";
    }

    /**
     * Handles GET requests to the run analysis fragment
     *
     * @param pathid the id of the workspace
     * @param workspaceWrapper the workspace being managed
     * @param account the account of the current user
     * @param isAjax whether or not the request was ajax or not
     * @param model holder for model attributes
     *
     * @return the path to the run template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/run")
    public String runGetFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("account") AccountWrapper account,
            @ModelAttribute("isAjax") boolean isAjax,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

        model.addAttribute("templates", TemplateWrapper.findByAccountAndPublicAndLanguage(account.getAccount(), templateRepository, workspaceWrapper.getLanguage()));
        return "dashboard/workspaces/fragments/run";
    }

    /**
     * Handles POST requests to the run analysis fragment
     *
     * @param pathid the id of the workspace
     * @param template_id the id of the template to run
     * @param workspaceWrapper the workspace being managed
     * @param account the account of the current user
     * @param isAjax whether or not the request was ajax or not
     * @param model holder for model attributes
     *
     * @return the path to the run template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     * @throws TemplateNotFound if the template was not found
     */
    @PostMapping("/dashboard/workspaces/manage/{pathid}/run")
    public String runPostFragment(
            @PathVariable("pathid") long pathid,
            @RequestParam(value="template_id", required=true) long template_id,
            @ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper,
            @ModelAttribute("account") AccountWrapper account,
            @ModelAttribute("isAjax") boolean isAjax,
            Model model
    ) throws NotAjaxRequest, TemplateNotFound {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces/manage/" + pathid);

        TemplateWrapper templateWrapper = new TemplateWrapper(template_id, account.getAccount(), templateRepository);

        try {
            workspaceWrapper.runTemplate(templateWrapper);
            model.addAttribute("success_msg", "workspaces.analysis.started");
        } catch (TemplateContainsNoDetectors e) {
            model.addAttribute("warning_msg", "workspaces.analysis.no_detectors");
        } catch (ClassNotFoundException | DetectorNotFound e) {
            model.addAttribute("warning_msg", "workspaces.analysis.detector_missing");
        } catch (ParameterNotFound e) {
            model.addAttribute("warning_msg", "workspaces.analysis.parameter_missing");
        } catch (NoFilesUploaded e) {
            model.addAttribute("warning_msg", "workspaces.analysis.no_files");
        }

        model.addAttribute("templates", TemplateWrapper.findByAccountAndPublic(account.getAccount(), templateRepository));
        return "dashboard/workspaces/fragments/run";
    }

    /**
     * Handles GET requests to the results fragment
     *
     * @param pathid the id of the workspace
     * @param workspaceWrapper the workspace being managed
     * @param isAjax whether or not the request was ajax or not
     * @param model holder for model attributes
     *
     * @return the path to the results fragment template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @GetMapping("/dashboard/workspaces/manage/{pathid}/results")
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

    /**
     * Handles GET requests to the delete workspace page
     *
     * @return the path to the delete template
     */
    @GetMapping("/dashboard/workspaces/{pathid}/delete")
    public String deleteGet() {
        return "dashboard/workspaces/delete";
    }

    /**
     * Handles POST requests to the delete workspace page
     *
     * @param workspaceWrapper the workspace being managed
     *
     * @return the path to the delete template
     */
    @PostMapping("/dashboard/workspaces/{pathid}/delete")
    public String deletePost(@ModelAttribute("workspace") WorkspaceWrapper workspaceWrapper) {
        workspaceWrapper.delete(workspaceRepository);
		return "redirect:/dashboard/workspaces?msg=deleted";
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
}