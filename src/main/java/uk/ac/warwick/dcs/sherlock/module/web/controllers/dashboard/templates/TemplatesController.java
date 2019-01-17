package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotAjaxRequest;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotTemplateOwner;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.TemplateForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.EngineDetectorWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.TemplateWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TemplateRepository;

import javax.validation.Valid;

@Controller
public class TemplatesController {
	@Autowired
	private TemplateRepository templateRepository;
	@Autowired
	private TDetectorRepository tDetectorRepository;

	public TemplatesController() { }

	@RequestMapping ("/dashboard/templates")
	public String indexGet() {
		return "dashboard/templates/index";
	}

	@RequestMapping ("/dashboard/templates/list")
	public String listGetFragment(
			@ModelAttribute("account") Account account,
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/templates");

		model.addAttribute(
				"templates",
				TemplateWrapper.findByAccountAndPublic(account, templateRepository)
		);

		return "dashboard/templates/fragments/list";
	}

	@GetMapping ("/dashboard/templates/add")
	public String addGet() {
		return "dashboard/templates/add";
	}

	@GetMapping ("/dashboard/templates/add/{language}")
	public String addGetFragment(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax,
			@PathVariable("language") Language language
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/templates/add");

		model.addAttribute("templateForm", new TemplateForm(language));
		model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(language));
		return "dashboard/templates/fragments/add";
	}

	@PostMapping("/dashboard/templates/add")
	public String addPostFragment(
			@Valid @ModelAttribute TemplateForm templateForm,
			BindingResult result,
			@ModelAttribute("account") Account account,
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotAjaxRequest, NotTemplateOwner {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/templates/add");

		if (result.hasErrors()) {
			model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(templateForm.getLanguage()));
			return "dashboard/templates/fragments/add";
		}

		TemplateWrapper templateWrapper = new TemplateWrapper(templateForm, account, templateRepository, tDetectorRepository);
		return "redirect:/dashboard/templates/manage/" + templateWrapper.getTemplate().getId();
	}
}
