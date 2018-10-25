package uk.ac.warwick.dcs.sherlock.module.web.controllers.workspaces;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

@Controller
public class WorkspacesController {

	public WorkspacesController() {
		EventBus.registerEventSubscriber(this);
	}

	@GetMapping ("/workspaces")
	public String index() {
		return "workspaces/index";
	}

}
