package uk.ac.warwick.dcs.sherlock.module.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Controller
public class SecurityController {

	@Autowired
	private Environment environment;

	@GetMapping ("/login")
	public String login(Model model) {
		//Automatically login if running locally
		if (Arrays.asList(environment.getActiveProfiles()).contains("client")) {
            model.addAttribute("local_username", "local.sherlock@example.com");
            model.addAttribute("local_password", "local_password");
            return "security/loginLocal";
		}

		return "security/login";
	}
}
