package uk.ac.warwick.dcs.sherlock.module.web.controllers.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;

@Controller
public class DashboardController {

//	private String result = "";

	public DashboardController() {
		EventBus.registerEventSubscriber(this);
	}

//	@EventHandler
//	public void getResults(EventPublishResults event) {
//		this.result = event.getResults();
//	}

	@GetMapping ("/dashboard")
	public String dashboard() {
		return "home/dashboard";
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
