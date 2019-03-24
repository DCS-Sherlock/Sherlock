package uk.ac.warwick.dcs.sherlock.module.web.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.warwick.dcs.sherlock.module.web.configuration.properties.WebmasterProperties;

/**
 * The controller that deals with the homepage for anonymous users
 */
@Controller
public class HomeController {
	/**
	 * Handles requests to the homepage, loads the contact details from the
	 * web master properties and redirects to the dashboard home if the
	 * user is logged in
	 *
	 * @param webmasterProperties details of the webmaster stored in the app properties
	 * @param model holder for model attributes
	 *
	 * @return the path to the homepage
	 */
	@RequestMapping ("/")
	public String index(WebmasterProperties webmasterProperties, Model model) {
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
