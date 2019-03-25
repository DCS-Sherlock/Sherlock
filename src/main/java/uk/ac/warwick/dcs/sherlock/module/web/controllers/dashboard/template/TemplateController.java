package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TParameterRepository;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.TemplateForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.EngineDetectorWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.TemplateWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TemplateRepository;

import javax.validation.Valid;

/**
 * The controller that deals with the manage template pages
 */
@Controller
public class TemplateController {
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TDetectorRepository tDetectorRepository;
    @Autowired
    private TParameterRepository tParameterRepository;

    /**
     * Handles GET requests to the manage template page
     *
     * @return the path to the manage template
     */
	@GetMapping("/dashboard/templates/manage/{pathid}")
	public String manageGet() {
		return "dashboard/templates/manage";
	}

    /**
     * Handles GET requests to the template details page
     *
     * @param pathid the id of the template
     * @param templateWrapper the template being managed
     * @param isAjax whether or not the request was ajax or not
     * @param model holder for model attributes
     *
     * @return the path to the details template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @GetMapping("/dashboard/templates/manage/{pathid}/details")
    public String detailsGetFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("template") TemplateWrapper templateWrapper,
            @ModelAttribute("isAjax") boolean isAjax,
            Model model
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/templates/manage/" + pathid);

        model.addAttribute("templateForm", new TemplateForm(templateWrapper));
        model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(templateWrapper.getTemplate().getLanguage()));
        model.addAttribute("languageList", SherlockRegistry.getLanguages());
        return "dashboard/templates/fragments/details";
    }

    /**
     * Handles POST requests to the details template page
     *
     * @param pathid the id of the template
     * @param templateWrapper the template being managed
     * @param isAjax  whether or not the request was ajax or not
     * @param templateForm the form that should be submitted in the request
     * @param result the results of the validation on the form above
     * @param model holder for model attributes
     *
     * @return the path to the details template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     * @throws NotTemplateOwner if the user attempts to modify a template that is public and not theirs
     */
    @PostMapping("/dashboard/templates/manage/{pathid}/details")
    public String detailsPostFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("template") TemplateWrapper templateWrapper,
            @ModelAttribute("isAjax") boolean isAjax,
            @Valid @ModelAttribute TemplateForm templateForm,
            BindingResult result,
            Model model
    ) throws NotAjaxRequest, NotTemplateOwner {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/templates/manage/" + pathid);

        if (!result.hasErrors()) {
            templateWrapper.update(templateForm, templateRepository, tDetectorRepository);
            model.addAttribute("success_msg", "templates.details.updated");
        }

        model.addAttribute("templateForm", templateForm);
        model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(templateWrapper.getTemplate().getLanguage()));
        model.addAttribute("languageList", SherlockRegistry.getLanguages());
        return "dashboard/templates/fragments/details";
    }

    /**
     * Handles GET requests to the template detectors page
     *
     * @param pathid the id of the template
     * @param templateWrapper the template being managed
     * @param isAjax whether or not the request was ajax or not
     *
     * @return the path of the detectors template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @GetMapping("/dashboard/templates/manage/{pathid}/detectors")
    public String detectorsGetFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("template") TemplateWrapper templateWrapper,
            @ModelAttribute("isAjax") boolean isAjax
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/templates/manage/" + pathid);

        return "dashboard/templates/fragments/detectors";
    }

    /**
     * Handles POST requests to the copy template page
     *
     * @param account the account of the current user
     * @param templateWrapper the template being managed
     *
     * @return a redirect to the new template
     */
    @PostMapping("/dashboard/templates/{pathid}/copy")
    public String copyPost(
            @ModelAttribute("account") AccountWrapper account,
            @ModelAttribute("template") TemplateWrapper templateWrapper
    ) {
        Template newTemplate = templateWrapper.copy(account, templateRepository, tDetectorRepository, tParameterRepository);
        return "redirect:/dashboard/templates/manage/" + newTemplate.getId();
    }

    /**
     * Handles GET requests to the delete template page
     *
     * @return the path to the delete page
     */
    @GetMapping("/dashboard/templates/{pathid}/delete")
    public String deleteGet() {
        return "dashboard/templates/delete";
    }

    /**
     * Handles POST requests to the delete template page
     *
     * @param templateWrapper the template being managed
     *
     * @return a redirect to the templates page
     *
     * @throws NotTemplateOwner if the user attempts to modify a template that is public and not theirs
     */
    @PostMapping("/dashboard/templates/{pathid}/delete")
    public String deletePost(
            @ModelAttribute("template") TemplateWrapper templateWrapper
    ) throws NotTemplateOwner {
        templateWrapper.delete(templateRepository);
        return "redirect:/dashboard/templates?msg=deleted_template";
    }

    /**
     * Handles GET requests to the template details fragment
     *
     * @param isAjax whether or not the request was ajax or not
     *
     * @return the path to the details template
     *
     * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
     */
    @GetMapping("/dashboard/templates/details/{pathid}")
    public String templateDetailsGetFragment(@ModelAttribute("isAjax") boolean isAjax) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces");
        return "dashboard/templates/fragments/template_details";
    }

    /**
     * Gets the template where the id equals the "pathid" path variable
     *
     * @param account the account of the current user
     * @param pathid the template id from the path variable
     * @param model holder for model attributes
     *
     * @return the template wrapper
     *
     * @throws TemplateNotFound if the template was not found
     */
	@ModelAttribute("template")
	private TemplateWrapper getTemplateWrapper(
            @ModelAttribute("account") AccountWrapper account,
            @PathVariable(value="pathid") long pathid,
            Model model)
        throws TemplateNotFound
    {
        TemplateWrapper templateWrapper = new TemplateWrapper(pathid, account.getAccount(), templateRepository);
        model.addAttribute("template", templateWrapper);
		return templateWrapper;
	}
}