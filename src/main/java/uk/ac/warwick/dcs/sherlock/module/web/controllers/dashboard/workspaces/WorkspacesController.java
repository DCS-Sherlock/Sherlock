package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.workspaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotAjaxRequest;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.WorkspaceForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

import javax.validation.Valid;

@Controller
public class WorkspacesController {
	@Autowired
	private WorkspaceRepository workspaceRepository;

	public WorkspacesController() { }

	@RequestMapping ("/dashboard/workspaces")
	public String indexGet() {
		return "dashboard/workspaces/index";
	}

	@RequestMapping ("/dashboard/workspaces/list")
	public String listGetFragment(
			@ModelAttribute("account") Account account,
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/workspaces");

		model.addAttribute(
				"workspaces",
				WorkspaceWrapper.findByAccount(account, workspaceRepository)
		);

		return "dashboard/workspaces/fragments/list";
	}

	@GetMapping ("/dashboard/workspaces/add")
	public String addGet(Model model) {
		model.addAttribute("workspaceForm", new WorkspaceForm());
		return "dashboard/workspaces/add";
	}

	@PostMapping("/dashboard/workspaces/add")
	public String addPost(
			@Valid @ModelAttribute WorkspaceForm workspaceForm,
			BindingResult result,
			@ModelAttribute("account") Account account
	) {
		if (result.hasErrors())
			return "dashboard/workspaces/add";

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspaceForm, account, workspaceRepository);
		return "redirect:/dashboard/workspaces/manage/" + workspaceWrapper.getId();
	}
}
