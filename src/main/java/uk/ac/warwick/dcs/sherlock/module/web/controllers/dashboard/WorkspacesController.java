package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
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
		return "dashboard/workspaces/index";
	}

	@RequestMapping ("/dashboard/workspaces/list")
	public String workspacesGetFragment(Model model, Authentication authentication) {
		List<Workspace> workspaces = workspaceRepository.findByAccount(this.getAccount(authentication));
		model.addAttribute("workspaces", workspaces);
		return "dashboard/workspaces/fragments/workspaces";
	}

	@GetMapping ("/dashboard/workspaces/add")
	public String addGet(Model model, HttpServletRequest request) {
		model = this.isAjax(model, request);
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
			model = this.isAjax(model, request);
			return "dashboard/workspaces/add";
		}

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspaceNameForm.getName(), this.getAccount(authentication));
		workspaceRepository.save(workspaceWrapper.getWorkspace()); //Todo: move to workspace wrapper
		return "redirect:/dashboard/workspaces";
	}

	@GetMapping ("/dashboard/workspaces/manage/{id}")
	public String manageGet(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null)
			return "redirect:/dashboard/workspaces?msg=notfound";

		model.addAttribute("workspace", workspace);
		return "dashboard/workspaces/manage";
	}

	@GetMapping ("/dashboard/workspaces/manage/name/{id}")
	public String nameGetFragment(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null)
			return "redirect:/dashboard/workspaces?msg=notfound";

		model.addAttribute("workspace", workspace);
		model.addAttribute("workspaceNameForm", new WorkspaceNameForm(workspace.getName()));
		return "dashboard/workspaces/fragments/name";
	}

	@PostMapping ("/dashboard/workspaces/manage/name/{id}")
	public String namePostFragment(
			@PathVariable(value="id") long id,
			@Valid @ModelAttribute WorkspaceNameForm workspaceNameForm,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null || !this.isAjax(request))
			return "redirect:/dashboard/workspaces?msg=notfound";

		if (!result.hasErrors()) {
			workspace.setName(workspaceNameForm.getName());
			workspaceRepository.save(workspace); //Todo: move to workspace wrapper
			result.reject("workspaces_message_updated_name"); //Todo: make message appear using "alert-success" not "alert-warning"
		}

		model.addAttribute("workspace", workspace);
		return "dashboard/workspaces/fragments/name";
	}


	@GetMapping ("/dashboard/workspaces/manage/submissions/{id}")
	public String submissionsGetFragment(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspace);

		model.addAttribute("workspace", workspaceWrapper.getWorkspace());
		model.addAttribute("fileUploadForm", new FileUploadForm());
		model.addAttribute("submissions", workspaceWrapper.getiWorkspace().getFiles());
		return "dashboard/workspaces/fragments/submissions";
	}

	@PostMapping ("/dashboard/workspaces/manage/submissions/{id}")
	public String submissionsPostFragment(
			@PathVariable(value="id") long id,
			@Valid @ModelAttribute FileUploadForm fileUploadForm,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		Workspace workspace = workspaceRepository.findByIdAndAccount(id, this.getAccount(authentication));

		if (workspace == null || !this.isAjax(request)) {
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
					} catch (WorkspaceUnsupportedException e) {
						e.printStackTrace(); // this is a major issue, we should probably quit here
					}
				}
			}
			result.reject("workspaces_message_uploaded_submission"); //Todo: make message appear using "alert-success" not "alert-warning"
		}

		model.addAttribute("workspace", workspaceWrapper.getWorkspace());
		model.addAttribute("submissions", workspaceWrapper.getiWorkspace().getFiles());
		return "dashboard/workspaces/fragments/submissions";
	}

	@GetMapping ("/dashboard/workspaces/manage/jobs/{id}")
	public String jobsGetFragment(
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
		model = this.isAjax(model, request);
		return "dashboard/workspaces/fragments/jobs";
	}

	@PostMapping ("/dashboard/workspaces/manage/jobs/{id}")
	public String jobsPostFragment(
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

		//test new code, remove this
		job.addDetector(TestDetector.class);
		job.addDetector(TestDetector.class);
		job.setParameter(SherlockRegistry.getDetectorAdjustableParameters(TestDetector.class).get(0), 7);
		job.prepare();

		//SherlockEngine.executor.submitJob(job);
		SherlockEngine.submitToExecutor(job); //temporary timed method for benchmarking, usually the above method would be used!

		model.addAttribute("workspace", workspace);
		model = this.isAjax(model, request);
		return "dashboard/workspaces/fragments/jobs";
	}

	@GetMapping ("/dashboard/workspaces/manage/results/{id}")
	public String resultsGetFragment(
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
		model = this.isAjax(model, request);
		return "dashboard/workspaces/fragments/results";
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
		model = this.isAjax(model, request);
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

	//Todo: Move helpers to separate class
	private boolean isAjax(HttpServletRequest request) {
		return request.getParameterMap().containsKey("ajax");
	}
	private Model isAjax(Model model, HttpServletRequest request) {
		model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
		return model;
	}
	private Account getAccount(Authentication authentication) {
		return accountRepository.findByEmail(authentication.getName());
	}

}
