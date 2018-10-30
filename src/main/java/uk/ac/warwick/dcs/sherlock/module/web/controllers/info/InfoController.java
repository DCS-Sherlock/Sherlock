package uk.ac.warwick.dcs.sherlock.module.web.controllers.info;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

@Controller
public class InfoController {

//	private String result = "";

	public InfoController() {
		EventBus.registerEventSubscriber(this);
	}

//	@EventHandler
//	public void getResults(EventPublishResults event) {
//		this.result = event.getResults();
//	}

	@GetMapping ("/")
	public String welcome() {
		//Redirect if the user is logged in
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return "redirect:dashboard/index";
		}

		return getReturn("info/index");
	}

	@GetMapping ("/info/terms")
	public String terms() {
		return getReturn("info/terms");
	}

	@GetMapping ("/info/about")
	public String about() {
		return getReturn("info/about");
	}

	@GetMapping ("/info/help")
	public String help() {
		return getReturn("info/help");
	}

	private String getReturn(String page) {
		//If running locally and logged out, automatically redirect to the login page
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (SherlockEngine.side == Side.CLIENT && auth instanceof AnonymousAuthenticationToken) {
			return "redirect:login";
		}

		return page;
	}

//	@GetMapping ("/greeting")
//	public String greeting(@RequestParam (name = "name", required = false, defaultValue = "World") String name, Model model) {
//		model.addAttribute("name", name);
//
//		List<String> detectors = RequestBus.post(new RequestDatabase.RegistryRequests.GetDetectorNames()).getResponce();
//		model.addAttribute("detectors", String.join(", ", detectors));
//
//		model.addAttribute("result", this.result);
//		return "greeting";
//	}

}
