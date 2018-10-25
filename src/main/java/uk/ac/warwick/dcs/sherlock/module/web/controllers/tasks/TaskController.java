package uk.ac.warwick.dcs.sherlock.module.web.controllers.tasks;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

@Controller
public class TaskController {

	public TaskController() {
		EventBus.registerEventSubscriber(this);
	}

	@GetMapping ("/tasks")
	public String index() {
		return "tasks/index";
	}

}
