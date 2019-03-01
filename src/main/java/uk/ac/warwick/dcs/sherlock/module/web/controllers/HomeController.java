package uk.ac.warwick.dcs.sherlock.module.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.warwick.dcs.sherlock.module.web.configuration.properties.WebmasterProperties;

@Controller
public class HomeController {
	@Autowired
	private WebmasterProperties webmasterProperties;

	public HomeController() { }

	@RequestMapping ("/")
	public String index(Model model) {
		//Redirect if the user is logged in
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return "redirect:dashboard/index";
		}

		model.addAttribute("institution", webmasterProperties.getInstitution());
		model.addAttribute("contact", webmasterProperties.getContact());
		model.addAttribute("link", webmasterProperties.getLink());

		return "home/index";
	}
}
