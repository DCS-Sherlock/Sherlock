package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

@Controller
public class WorkspacesController {

	public WorkspacesController() {
		EventBus.registerEventSubscriber(this);
	}

	@GetMapping ("/dashboard/workspaces/add")
	public String addGet(Model model) {
		//		List<String> detectors = RequestBus.post(new RequestDatabase.RegistryRequests.GetDetectorNames()).getResponse();
		//		model.addAttribute("detectors", String.join(", ", detectors));
		return "dashboard/workspaces/add";
	}

	@PostMapping ("/dashboard/workspaces/add")
	public String addPost() {
		return "dashboard/workspaces/add";
	}

	@RequestMapping ("/dashboard/workspaces")
	public String indexGet() {
		return "dashboard/workspaces/index";
	}

	@GetMapping ("/dashboard/workspaces/manage")
	public String manageGet() {
		return "dashboard/workspaces/manage";
	}

	@PostMapping ("/dashboard/workspaces/manage")
	public String managePost() {
		return "dashboard/workspaces/manage";
	}

}
