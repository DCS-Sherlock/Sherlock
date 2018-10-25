package uk.ac.warwick.dcs.sherlock.module.web.controllers.accounts;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.warwick.dcs.sherlock.api.annotations.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;
import uk.ac.warwick.dcs.sherlock.api.event.EventPublishResults;
import uk.ac.warwick.dcs.sherlock.api.request.RequestBus;
import uk.ac.warwick.dcs.sherlock.api.request.RequestDatabase;

import java.util.List;

@Controller
public class LoginController {

	public LoginController() {
		EventBus.registerEventSubscriber(this);
	}

	@GetMapping ("/login")
	public String login() {
		return "accounts/login";
	}

	@GetMapping ("/register")
	public String register() {
		return "accounts/register";
	}

	@GetMapping ("/logout")
	public String logout() {
		return "accounts/logout";
	}

}
