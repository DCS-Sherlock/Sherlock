package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotAjaxRequest;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.WorkspaceForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.WorkspaceRepository;

import javax.validation.Valid;

/**
 * The controller that deals with the workspaces page
 */
@Controller
public class WorkspacesController {
	@Autowired
	private WorkspaceRepository workspaceRepository;

	/**
	 * Handles GET requests to the workspaces page
	 *
	 * @return the path to the workspaces template
	 */
	@GetMapping ("/dashboard/workspaces")
	public String indexGet() {
		return "dashboard/workspaces/index";
	}

	/**
	 * Handles GET requests to the workspaces list fragment
	 *
	 * @param account the account object of the current user
	 * @param isAjax whether or not the request was ajax or not
	 * @param model holder for model attributes
	 *
	 * @throws NotAjaxRequest if the request was not an ajax one, the message is where to redirect the user to
	 *
	 * @return the path to the list fragment template
	 */
	@GetMapping ("/dashboard/workspaces/list")
	public String listGetFragment(
			@ModelAttribute("account") AccountWrapper account,
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces");

		model.addAttribute(
				"workspaces",
				WorkspaceWrapper.findByAccount(account.getAccount(), workspaceRepository)
		);

		return "dashboard/workspaces/fragments/list";
	}

	/**
	 * Handles GET requests to the add workspace page
	 *
	 * @param model holder for model attributes
	 *
	 * @return the path to the add workspace template
	 */
	@GetMapping ("/dashboard/workspaces/add")
	public String addGet(Model model) {
		model.addAttribute("workspaceForm", new WorkspaceForm());
		model.addAttribute("languageList", SherlockRegistry.getLanguages());
		return "dashboard/workspaces/add";
	}

	/**
	 * Handles POST requests to the add workspace page
	 *
	 * @param workspaceForm the form that should be submitted in the request
	 * @param result the results of the validation on the form above
	 * @param account the account object of the current user
	 * @param model holder for model attributes
	 *
	 * @return the path to the add workspace template
	 */
	@PostMapping("/dashboard/workspaces/add")
	public String addPost(
			@Valid @ModelAttribute WorkspaceForm workspaceForm,
			BindingResult result,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (result.hasErrors()) {
			model.addAttribute("languageList", SherlockRegistry.getLanguages());
			return "dashboard/workspaces/add";
		}

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspaceForm, account.getAccount(), workspaceRepository);
		return "redirect:/dashboard/workspaces/manage/" + workspaceWrapper.getId();
	}
}
