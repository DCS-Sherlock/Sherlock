package uk.ac.warwick.dcs.sherlock.module.web.controllers.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

@Controller
public class HomeController {

//	private String result = "";

	public HomeController() {
		EventBus.registerEventSubscriber(this);
	}

//	@EventHandler
//	public void getResults(EventPublishResults event) {
//		this.result = event.getResults();
//	}

	@GetMapping ("/")
	public String welcome() {
		return "home/welcome";
	}

	@GetMapping ("/terms")
	public String terms() {
		return "home/terms";
	}

	@GetMapping ("/about")
	public String about() {
		return "home/about";
	}

	@GetMapping ("/help")
	public String help() {
		return "home/help";
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
