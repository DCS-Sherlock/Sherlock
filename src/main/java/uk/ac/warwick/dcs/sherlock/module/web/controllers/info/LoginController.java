package uk.ac.warwick.dcs.sherlock.module.web.controllers.info;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Controller
public class LoginController {

	@Autowired
	private Environment environment;

	@GetMapping ("/login")
	public String login(HttpServletRequest request) {
		//Automatically login if running locally
		if (Arrays.asList(environment.getActiveProfiles()).contains("client")) {
			try {
				request.login("local_user", "local_password");
			}
			catch (ServletException e) { }
		}

		//Redirect if the user is logged in
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return "redirect:dashboard/index";
		}

		return "info/login";
	}

	@GetMapping ("/register")
	public String register() {
		//If running locally, redirect to login page
		if (Arrays.asList(environment.getActiveProfiles()).contains("client")) {
			return "redirect:login";
		}

		//Redirect if the user is logged in
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return "redirect:dashboard/index";
		}

		return "info/register";
	}
}
