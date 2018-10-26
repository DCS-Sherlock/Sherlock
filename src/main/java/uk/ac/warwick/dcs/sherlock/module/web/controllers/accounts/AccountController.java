package uk.ac.warwick.dcs.sherlock.module.web.controllers.accounts;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

@Controller
public class AccountController {

	public AccountController() {
		EventBus.registerEventSubscriber(this);
	}

	@GetMapping ("/account")
	public String account() {
		return "accounts/account";
	}

}
