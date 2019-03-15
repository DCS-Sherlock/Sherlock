package uk.ac.warwick.dcs.sherlock.module.web.controllers;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

/**
 * The controller that deals with the login page
 */
@Controller
public class SecurityController {
	/**
	 * Handles requests to the login page, automatically fills in the
	 * login form when running as a client
	 *
	 * @param environment the details about the spring boot environment
	 * @param model holder for model attributes
	 *
	 * @return the path to the login page
	 */
	@GetMapping ("/login")
	public String login(Environment environment, Model model) {
		//Automatically login if running locally
		if (Arrays.asList(environment.getActiveProfiles()).contains("client")) {
            model.addAttribute("local_username", "local.sherlock@example.com");
            model.addAttribute("local_password", "local_password");
            return "security/loginLocal";
		}

		return "security/login";
	}
}
