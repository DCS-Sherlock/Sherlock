package uk.ac.warwick.dcs.sherlock.module.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.warwick.dcs.sherlock.api.annotations.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;
import uk.ac.warwick.dcs.sherlock.api.request.RequestBus;
import uk.ac.warwick.dcs.sherlock.api.request.RequestDatabase;
import uk.ac.warwick.dcs.sherlock.api.event.EventPublishResults;

import java.util.*;

@Controller
public class HomeController {

	private String result = "";

	public HomeController() {
		EventBus.registerEventSubscriber(this);
	}

	@EventHandler
	public void getResults(EventPublishResults event) {
		this.result = event.getResults();
	}

	@GetMapping ("/")
	public String index(Model model) {
		return "index";
	}

	@GetMapping ("/greeting")
	public String greeting(@RequestParam (name = "name", required = false, defaultValue = "World") String name, Model model) {
		model.addAttribute("name", name);

		List<String> detectors = RequestBus.post(new RequestDatabase.RegistryRequests.GetDetectorNames()).getResponce();
		model.addAttribute("detectors", String.join(", ", detectors));

		model.addAttribute("result", this.result);
		return "greeting";
	}

}
