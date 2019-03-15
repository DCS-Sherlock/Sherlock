package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

/**
 * The controller that deals with the dashboard homepage
 */
@Controller
public class DashboardController {
	/**
	 * Handles GET requests to the dashboard home
	 *
	 * @return the path to the dashboard page
	 */
	@GetMapping ("/dashboard/index")
	public String index() {
		return "dashboard/index";
	}
}