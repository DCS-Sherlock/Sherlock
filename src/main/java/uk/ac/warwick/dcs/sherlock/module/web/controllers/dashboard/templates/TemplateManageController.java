package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.TemplateForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.EngineDetectorWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.TemplateWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TemplateRepository;

import javax.validation.Valid;

@Controller
public class TemplateManageController {
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TDetectorRepository tDetectorRepository;

    public TemplateManageController() { }

	@GetMapping("/dashboard/templates/manage/{pathid}")
	public String manageGet() {
		return "dashboard/templates/manage";
	}

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

    @GetMapping("/dashboard/templates/manage/{pathid}/detectors")
    public String detectorsGetFragment(
            @PathVariable("pathid") long pathid,
            @ModelAttribute("template") TemplateWrapper templateWrapper,
            @ModelAttribute("isAjax") boolean isAjax
    ) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/templates/manage/" + pathid);

        return "dashboard/templates/fragments/detectors";
    }

    @GetMapping("/dashboard/templates/{pathid}/delete")
    public String deleteGet() {
        return "dashboard/templates/delete";
    }

    @PostMapping("/dashboard/templates/{pathid}/delete")
    public String deletePost(@ModelAttribute("template") TemplateWrapper templateWrapper) throws NotTemplateOwner {
        templateWrapper.delete(templateRepository);
        return "redirect:/dashboard/templates?msg=deleted";
    }

    @GetMapping("/dashboard/templates/details/{pathid}")
    public String templateDetailsGet(@ModelAttribute("isAjax") boolean isAjax) throws NotAjaxRequest {
        if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces");
        return "dashboard/templates/fragments/template_details";
    }

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