package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Workspace;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.WorkspaceNameForm;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@SuppressWarnings("Duplicates")
public class WorkspacesController {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private WorkspaceRepository workspaceRepository;

	public WorkspacesController() { }

	@RequestMapping ("/dashboard/workspaces")
	public String indexGet(Model model, Authentication authentication) {
		List<Workspace> workspaces = workspaceRepository.findByAccount(this.getAccount(authentication));
		model.addAttribute("workspaces", workspaces);
		return "dashboard/workspaces/index";
	}

	@GetMapping ("/dashboard/workspaces/add")
	public String addGet(Model model, HttpServletRequest request) {
		model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
		model.addAttribute("workspaceNameForm", new WorkspaceNameForm());
		return "dashboard/workspaces/add";
	}

	@PostMapping("/dashboard/workspaces/add")
	public String addPost(
			@Valid @ModelAttribute WorkspaceNameForm workspaceNameForm,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		if (result.hasErrors()){
			model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
			return "dashboard/workspaces/add";
		}

		workspaceRepository.save(new Workspace(workspaceNameForm.getName(), this.getAccount(authentication)));

		return "redirect:/dashboard/workspaces";
	}

	@GetMapping ("/dashboard/workspaces/manage/{id}")
	public String manage(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspace);
		return "dashboard/workspaces/manage";
	}

	@GetMapping ("/dashboard/workspaces/manage/name/{id}")
	public String manageNameGet(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null || !request.getParameterMap().containsKey("ajax")) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		WorkspaceNameForm workspaceNameForm = new WorkspaceNameForm(workspace.getName());
		model.addAttribute("workspace", workspace);
		model.addAttribute("workspaceNameForm", workspaceNameForm);
		return "dashboard/workspaces/manageName";
	}

	@PostMapping ("/dashboard/workspaces/manage/name/{id}")
	public String manageNamePost(
			@PathVariable(value="id") long id,
			@Valid @ModelAttribute WorkspaceNameForm workspaceNameForm,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null || !request.getParameterMap().containsKey("ajax")) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		if (!result.hasErrors()) {
			workspace.setName(workspaceNameForm.getName());
			workspaceRepository.save(workspace);
			result.reject("workspaces_message_updated_name");
		}

		model.addAttribute("workspace", workspace);
		return "dashboard/workspaces/manageName";
	}

	@GetMapping ("/dashboard/workspaces/delete/{id}")
	public String deleteGet(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspace);
		model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
		return "dashboard/workspaces/delete";
	}

	@PostMapping ("/dashboard/workspaces/delete/{id}")
	public String deletePost(
			@PathVariable(value="id") long id,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		workspaceRepository.deleteById(id);

		return "redirect:/dashboard/workspaces?msg=deleted";
	}

	private Account getAccount(Authentication authentication) {
		return accountRepository.findByEmail(authentication.getName());
	}

}
