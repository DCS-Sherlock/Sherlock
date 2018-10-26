package uk.ac.warwick.dcs.sherlock.module.web.controllers.accounts;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

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
