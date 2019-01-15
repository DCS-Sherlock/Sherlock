package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.TemplateForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.EngineDetectorWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TemplateDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TemplateRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@SuppressWarnings("Duplicates")
public class TemplatesController {
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TemplateRepository templateRepository;
	@Autowired
	private TemplateDetectorRepository templateDetectorRepository;

	public TemplatesController() { }

	@RequestMapping ("/dashboard/templates")
	public String indexGet() {
		return "dashboard/templates/index";
	}

	@RequestMapping ("/dashboard/templates/list")
	public String listGetFragment(Model model, HttpServletRequest request, Authentication authentication) {
		if (!this.isAjax(request))
			return "redirect:/dashboard/templates";

		model.addAttribute(
				"templates",
				templateRepository.findAccountAndPublic(this.getAccount(authentication))
		);
		return "dashboard/templates/fragments/list";
	}

	@GetMapping ("/dashboard/templates/add")
	public String addGet(Model model, HttpServletRequest request) {
		model = this.isAjax(model, request);
		return "dashboard/templates/add";
	}

	@GetMapping ("/dashboard/templates/add/{lang}")
	public String addGetFragment(
			Model model,
			HttpServletRequest request,
			@PathVariable(value="lang") Language language
			) {
		if (!this.isAjax(request))
			return "redirect:/dashboard/templates/add";

		model.addAttribute("templateForm", new TemplateForm(language));
		model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(language));
		model.addAttribute("language", language);
		return "dashboard/templates/fragments/add";
	}

	@PostMapping("/dashboard/templates/add")
	public String addPostFragment(
			@Valid @ModelAttribute TemplateForm templateForm,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		if (!this.isAjax(request))
			return "redirect:/dashboard/templates/add";

		if (result.hasErrors()){
			model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(templateForm.getLanguage()));
			return "dashboard/templates/fragments/add";
		}

		Template template = new Template(this.getAccount(authentication));
		templateForm.updateTemplate(template, templateRepository, templateDetectorRepository);

		return "redirect:/dashboard/templates/manage/"+template.getId();
	}

	@GetMapping ("/dashboard/templates/manage/{id}")
	public String manageGet(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		Template template = templateRepository.findByIdAndPublic(id, this.getAccount(authentication));

		if (template == null) {
			return "redirect:/dashboard/templates?msg=notfound";
		}

		model = this.isOwner(model, authentication, template);
		model.addAttribute("template", template);
		return "dashboard/templates/manage";
	}

	@GetMapping ("/dashboard/templates/manage/details/{id}")
	public String detailsGetFragment(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		if (!this.isAjax(request))
			return "redirect:/dashboard/templates/manage/" + id;

		Template template = templateRepository.findByIdAndPublic(id, this.getAccount(authentication));

		if (template == null) {
			return "redirect:/dashboard/templates?msg=notfound";
		}

		model = this.isOwner(model, authentication, template);
		model.addAttribute("template", template);
		model.addAttribute("templateForm", new TemplateForm(template));
		model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(template.getLanguage()));
		return "dashboard/templates/fragments/details";
	}

	@PostMapping ("/dashboard/templates/manage/details/{id}")
	public String detailsPostFragment(
			@Valid @ModelAttribute TemplateForm templateForm,
			BindingResult result,
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		if (!this.isAjax(request))
			return "redirect:/dashboard/templates/manage/" + id + "?msg=ajax";

		Template template = templateRepository.findById(id).get();

		if (template == null || !this.isOwner(authentication, template)) {
			return "redirect:/dashboard/templates?msg=notfound";
		}

		if (!result.hasErrors()){
			templateForm.updateTemplate(template, templateRepository, templateDetectorRepository);
			result.reject("templates_message_updated_details"); //Todo: make message appear using "alert-success" not "alert-warning"
		}

		model = this.isOwner(model, authentication, template);
		model.addAttribute("template", template);
		model.addAttribute("detectorList", EngineDetectorWrapper.getDetectors(template.getLanguage()));
		return "dashboard/templates/fragments/details";
	}

	@GetMapping ("/dashboard/templates/delete/{id}")
	public String deleteGet(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Template template = templateRepository.findById(id).get();

		if (template == null || !this.isOwner(authentication, template)) {
			return "redirect:/dashboard/templates?msg=notfound";
		}

		model.addAttribute("template", template);
		model = this.isAjax(model, request);
		return "dashboard/templates/delete";
	}

	@PostMapping ("/dashboard/templates/delete/{id}")
	public String deletePost(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		Template template = templateRepository.findById(id).get();

		if (template == null || !this.isOwner(authentication, template)) {
			return "redirect:/dashboard/templates?msg=notfound";
		}

		templateRepository.deleteById(id);

		return "redirect:/dashboard/templates?msg=deleted";
	}

	//Todo: Move helpers to separate class
	private boolean isAjax(HttpServletRequest request) {
		return request.getParameterMap().containsKey("ajax");
	}
	private boolean isOwner(Authentication authentication, Template template) {
		if (template.getAccount().getId() == this.getAccount(authentication).getId()) {
			return true;
		}
		return false;
	}
	private Model isOwner(Model model, Authentication authentication, Template template) {
		model.addAttribute("owner", false);
		if (template.getAccount().getId() == this.getAccount(authentication).getId()) {
			model.addAttribute("owner", true);
		}
		return model;
	}
	private Model isAjax(Model model, HttpServletRequest request) {
		model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
		return model;
	}
	private Account getAccount(Authentication authentication) {
		return accountRepository.findByEmail(authentication.getName());
	}

}
