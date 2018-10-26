package uk.ac.warwick.dcs.sherlock.module.web.controllers.accounts;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

@Controller
public class ManageController {

	public ManageController() {
		EventBus.registerEventSubscriber(this);
	}

	@GetMapping ("/accounts/manage")
	public String manage() {
		return "accounts/manage";
	}

}
