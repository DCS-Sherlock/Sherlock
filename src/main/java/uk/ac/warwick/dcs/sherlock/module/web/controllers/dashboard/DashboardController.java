package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotAjaxRequest;

/**
 * The controller that deals with the dashboard homepage
 */
@Controller
public class DashboardController {
	/**
	 * Handles GET requests to the dashboard home
	 *
	 * @return the path to the dashboard page
	 */
	@GetMapping ("/dashboard/index")
	public String index() {
		return "dashboard/index";
	}


	@GetMapping ("/dashboard/index/queue")
	public String queueGetFragment(
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/index");

		model.addAttribute(
				"jobs",
				SherlockEngine.executor.getAllJobStatuses()
		);

		model.addAttribute("executor", SherlockEngine.executor);

		return "dashboard/fragments/queue";
	}


	@GetMapping ("/dashboard/index/statistics")
	public String statisticsGetFragment(
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/dashboard/index");

//		model.addAttribute(
//				"jobs",
//				SherlockEngine.executor.getAllJobStatuses()
//		);

		return "dashboard/fragments/statistics";
	}
}