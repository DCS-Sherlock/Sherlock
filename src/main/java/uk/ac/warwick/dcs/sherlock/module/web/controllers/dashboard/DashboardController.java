package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

@Controller
public class DashboardController {

	public DashboardController() {
		EventBus.registerEventSubscriber(this);
	}

	@GetMapping ("/dashboard/index")
	public String index() {
		return "dashboard/index";
	}
}