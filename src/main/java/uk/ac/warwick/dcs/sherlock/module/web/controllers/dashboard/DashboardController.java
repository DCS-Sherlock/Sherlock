package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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


	/* This should poll SherlockEngine.executor.getAllJobStatuses()
	 * - it returns a sorted list of all job statuses, for jobs running or recently finished (currently within 1 minute, to be adjusted after dash implemented).
	 *
	 *  - JobStatus.getID() returns a unique id for the job status, implemented to help track which job status' are present, are new, have moved in the sorted list
	 *    and are removed in the UI. Should help optimise the update step.
	 *
	 *  - JobStatus.getFormattedDuration() returns the current runtime OR the final job runtime if finished as a string
	 *  - JobStatus.getMessage() returns the message explaining the current step, or an error msg if failed (error msg not in yet)
	 *  - JobStatus.isFinished() returns if job is finished
	 *  - JobStatus.getProgress() returns the progress of a job as percentage (is float between 0 and 1). Ideally use for a progress bar. Currently not working.
	 */
}