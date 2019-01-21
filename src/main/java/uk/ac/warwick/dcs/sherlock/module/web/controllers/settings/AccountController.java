package uk.ac.warwick.dcs.sherlock.module.web.controllers.settings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

	@GetMapping ("/account")
	public String index() {
		return "settings/account";
	}
}