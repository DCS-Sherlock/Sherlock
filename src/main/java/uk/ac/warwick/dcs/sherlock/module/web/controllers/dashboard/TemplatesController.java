package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;
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

/**
 * The controller that deals with the templates pages
 */
@Controller
public class TemplatesController {
	@Autowired
	private TemplateRepository templateRepository;
	@Autowired
	private TDetectorRepository tDetectorRepository;

	/**
	 * Handles GET requests to the templates page
	 *
	 * @return the path to the templates page template
	 */
	@GetMapping ("/dashboard/templates")
	public String indexGet() {
		return "dashboard/templates/index";
	}

	/**
	 * Handles GET requests to the template list page
	 *
	 * @param account the account of the current user
	 * @param isAjax whether or not the request was ajax or not
	 * @param model holder for model attributes
	 *
	 * @return the path to the template list page template
	 *
	 * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
	 */
	@GetMapping ("/dashboard/templates/list")
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

	/**
	 * Handles GET requests to the add template page
	 *
	 * @param model holder for model attributes
	 *
	 * @return the path to the add template page template
	 */
	@GetMapping ("/dashboard/templates/add")
	public String addGet(Model model) {
		Set<String> languages = SherlockRegistry.getLanguages();
		String language = languages.iterator().next();
		model.addAttribute("templateForm", new TemplateForm(language));
		model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(language));
		model.addAttribute("languageList", languages);
		return "dashboard/templates/add";
	}

	/**
	 * Handles POST requests to the add template page
	 *
	 * @param templateForm the form that should be submitted in the request
	 * @param result the results of the validation on the form above
	 * @param account the account of the current user
	 * @param isAjax whether or not the request was ajax or not
	 * @param model holder for model attributes
	 *
	 * @return the path to the add template page template
	 *
	 * @throws NotTemplateOwner if the user attempts to modify a template that is public and not theirs
	 */
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

	/**
	 * Handles GET requests to the detectors list page, which lists
	 * the detectors available for the specified language
	 *
	 * @param model holder for model attributes
	 * @param isAjax whether or not the request was ajax or not
	 * @param language the language to fetch the detectors for
	 *
	 * @return the path to the detectors list template
	 *
	 * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
	 */
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
