package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.model.IJob;
import uk.ac.warwick.dcs.sherlock.engine.model.ITask;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Workspace;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.FileUploadForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.WorkspaceNameForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.WorkspaceRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
@SuppressWarnings("Duplicates")
public class WorkspacesController {
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private WorkspaceRepository workspaceRepository;

	public WorkspacesController() { }

	@RequestMapping ("/dashboard/workspaces")
	public String indexGet(Model model, Authentication authentication) {
		List<Workspace> workspaces = workspaceRepository.findByAccount(this.getAccount(authentication));
		model.addAttribute("workspaces", workspaces);
		return "dashboard/workspaces/index";
	}

	@GetMapping ("/dashboard/workspaces/add")
	public String addGet(Model model, HttpServletRequest request) {
		model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
		model.addAttribute("workspaceNameForm", new WorkspaceNameForm());
		return "dashboard/workspaces/add";
	}

	@PostMapping("/dashboard/workspaces/add")
	public String addPost(
			@Valid @ModelAttribute WorkspaceNameForm workspaceNameForm,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		if (result.hasErrors()){
			model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
			return "dashboard/workspaces/add";
		}

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspaceNameForm.getName(), this.getAccount(authentication));

		workspaceRepository.save(workspaceWrapper.getWorkspace());

		return "redirect:/dashboard/workspaces";
	}

	@GetMapping ("/dashboard/workspaces/manage/{id}")
	public String manage(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspace);
		return "dashboard/workspaces/manage";
	}

	@GetMapping ("/dashboard/workspaces/manage/name/{id}")
	public String manageNameGet(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null || !request.getParameterMap().containsKey("ajax")) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		WorkspaceNameForm workspaceNameForm = new WorkspaceNameForm(workspace.getName());
		model.addAttribute("workspace", workspace);
		model.addAttribute("workspaceNameForm", workspaceNameForm);
		return "dashboard/workspaces/manageName";
	}

	@PostMapping ("/dashboard/workspaces/manage/name/{id}")
	public String manageNamePost(
			@PathVariable(value="id") long id,
			@Valid @ModelAttribute WorkspaceNameForm workspaceNameForm,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null || !request.getParameterMap().containsKey("ajax")) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		if (!result.hasErrors()) {
			workspace.setName(workspaceNameForm.getName());
			workspaceRepository.save(workspace);
			result.reject("workspaces_message_updated_name");
		}

		model.addAttribute("workspace", workspace);
		return "dashboard/workspaces/manageName";
	}


	@GetMapping ("/dashboard/workspaces/manage/submissions/{id}")
	public String manageSubmissionsGet(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null || !request.getParameterMap().containsKey("ajax")) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspace);

		model.addAttribute("workspace", workspace);
		model.addAttribute("fileUploadForm", new FileUploadForm());
		model.addAttribute("submissions", workspaceWrapper.getiWorkspace().getFiles());
		return "dashboard/workspaces/manageSubmissions";
	}

	@PostMapping ("/dashboard/workspaces/manage/submissions/{id}")
	public String manageSubmissionsPost(
			@PathVariable(value="id") long id,
			@Valid @ModelAttribute FileUploadForm fileUploadForm,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null || !request.getParameterMap().containsKey("ajax")) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspace);

		if (!result.hasErrors()) {
			for(MultipartFile file : fileUploadForm.getFiles()) {
				if (file.getSize() > 0) {
					try {
						SherlockEngine.storage.storeFile(workspaceWrapper.getiWorkspace(), file.getOriginalFilename(), file.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
					catch (WorkspaceUnsupportedException e) {
						e.printStackTrace(); // this is a major issue, we should probably quit here
					}
				}
			}
			result.reject("workspaces_message_uploaded_submission");
		}

		model.addAttribute("workspace", workspace);
		model.addAttribute("submissions", workspaceWrapper.getiWorkspace().getFiles());
		return "dashboard/workspaces/manageSubmissions";
	}

	@GetMapping ("/dashboard/workspaces/manage/jobs/{id}")
	public String manageJobsGet(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspace);
		model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
		return "dashboard/workspaces/manageJobs";
	}

	@PostMapping ("/dashboard/workspaces/manage/jobs/{id}")
	public String manageJobsPost(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspace);
		IJob job = workspaceWrapper.getiWorkspace().createJob();
		ITask task = job.createTask(new TestDetector());

		model.addAttribute("workspace", workspace);
		model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
		return "dashboard/workspaces/manageJobs";
	}

	@GetMapping ("/dashboard/workspaces/manage/results/{id}")
	public String manageResultsGet(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspace);
		List<IJob> jobs = workspaceWrapper.getiWorkspace().getJobs();
		model.addAttribute("jobs", jobs);
//		jobs.get(0).getTasks().get(0).getRawResults().get(0).

		model.addAttribute("workspace", workspace);
		model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
		return "dashboard/workspaces/manageResults";
	}

	@GetMapping ("/dashboard/workspaces/delete/{id}")
	public String deleteGet(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspace);
		model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
		return "dashboard/workspaces/delete";
	}

	@PostMapping ("/dashboard/workspaces/delete/{id}")
	public String deletePost(
			@PathVariable(value="id") long id,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		workspaceRepository.deleteById(id);

		return "redirect:/dashboard/workspaces?msg=deleted";
	}

	private Account getAccount(Authentication authentication) {
		return accountRepository.findByEmail(authentication.getName());
	}

}
