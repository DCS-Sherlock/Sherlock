package uk.ac.warwick.dcs.sherlock.module.web.controllers.settings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

@Controller
public class SettingsController {

	public SettingsController() {
		EventBus.registerEventSubscriber(this);
	}

	@GetMapping ("/settings")
	public String index() {
		return "settings/index";
	}

}
