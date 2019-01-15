package uk.ac.warwick.dcs.sherlock.module.web.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

@Controller
public class HelpController {
	public HelpController() { }

	@RequestMapping ("/help")
	public String index() {
		return "help/index";
	}

	@RequestMapping ("/terms")
	public String terms() {
		return "help/terms";
	}

	@RequestMapping ("/privacy")
	public String privacy() {
		return "help/privacy";
	}

}
