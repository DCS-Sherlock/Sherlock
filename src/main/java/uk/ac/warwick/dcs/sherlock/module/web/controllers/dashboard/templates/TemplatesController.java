package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotAjaxRequest;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotTemplateOwner;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.TemplateForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.EngineDetectorWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.TemplateWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TemplateRepository;

import javax.validation.Valid;
import java.util.Set;

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
			@ModelAttribute("account") AccountWrapper account,
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/templates");

		model.addAttribute(
				"templates",
				TemplateWrapper.findByAccountAndPublic(account.getAccount(), templateRepository)
		);

		return "dashboard/templates/fragments/list";
	}

	@GetMapping ("/dashboard/templates/add")
	public String addGet(Model model) {
		Set<String> languages = SherlockRegistry.getLanguages();
		String language = languages.iterator().next();
		model.addAttribute("templateForm", new TemplateForm(language));
		model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(language));
		model.addAttribute("languageList", languages);
		return "dashboard/templates/add";
	}

	@PostMapping("/dashboard/templates/add")
	public String addPost(
			@Valid @ModelAttribute TemplateForm templateForm,
			BindingResult result,
			@ModelAttribute("account") AccountWrapper account,
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotTemplateOwner {
		if (result.hasErrors()) {
			model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(templateForm.getLanguage()));
			model.addAttribute("languageList", SherlockRegistry.getLanguages());
			return "dashboard/templates/add";
		}

		TemplateWrapper templateWrapper = new TemplateWrapper(templateForm, account.getAccount(), templateRepository, tDetectorRepository);
		return "redirect:/dashboard/templates/manage/" + templateWrapper.getTemplate().getId();
	}

	@GetMapping ("/dashboard/templates/detectors/{language}")
	public String detectorsGetFragment(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax,
			@PathVariable("language") String language
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/templates");

		model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(language));
		return "dashboard/templates/fragments/details_detectors";
	}
}
