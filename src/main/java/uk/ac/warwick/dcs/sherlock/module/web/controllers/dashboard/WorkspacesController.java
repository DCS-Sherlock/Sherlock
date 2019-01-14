package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.IWorkspaceNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.WorkspaceNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TemplateDetector;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Workspace;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.FileUploadForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.WorkspaceNameForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TemplateRepository;
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
	@Autowired
	private TemplateRepository templateRepository;

	public WorkspacesController() { }

	@RequestMapping ("/dashboard/workspaces")
	public String indexGet() {
		return "dashboard/workspaces/index";
	}

	@RequestMapping ("/dashboard/workspaces/list")
	public String listGetFragment(Model model, Authentication authentication) {
		model.addAttribute(
				"workspaces",
				WorkspaceWrapper.getWorkspacesByAccount(this.getAccount(authentication), workspaceRepository)
		);
		return "dashboard/workspaces/fragments/list";
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

		WorkspaceWrapper workspaceWrapper = new WorkspaceWrapper(workspaceNameForm.getName(), Language.JAVA, this.getAccount(authentication), workspaceRepository);

		return "redirect:/dashboard/workspaces/manage/" + workspaceWrapper.getId();
	}

	@GetMapping ("/dashboard/workspaces/manage/{id}")
	public String manageGet(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		WorkspaceWrapper workspaceWrapper;
		try {
			workspaceWrapper = new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspaceWrapper);
		return "dashboard/workspaces/manage";
	}

	@GetMapping ("/dashboard/workspaces/manage/name/{id}")
	public String nameGetFragment(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		WorkspaceWrapper workspaceWrapper;
		try {
			workspaceWrapper = new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspaceWrapper);
		model.addAttribute("workspaceNameForm", new WorkspaceNameForm(workspaceWrapper.getName()));
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
		if (!this.isAjax(request))
			return "redirect:/dashboard/workspaces?msg=ajax";

		WorkspaceWrapper workspaceWrapper;
		try {
			workspaceWrapper = new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		if (!result.hasErrors()) {
			workspaceWrapper.setName(workspaceNameForm.getName());
			result.reject("workspaces_message_updated_name"); //Todo: make message appear using "alert-success" not "alert-warning"
		}

		model.addAttribute("workspace", workspaceWrapper);
		return "dashboard/workspaces/fragments/name";
	}


	@GetMapping ("/dashboard/workspaces/manage/submissions/{id}")
	public String submissionsGetFragment(
			@PathVariable(value="id") long id,
			Model model,
			Authentication authentication
	) {
		WorkspaceWrapper workspaceWrapper;
		try {
			workspaceWrapper = new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspaceWrapper);
		model.addAttribute("fileUploadForm", new FileUploadForm());
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
		WorkspaceWrapper workspaceWrapper;
		try {
			workspaceWrapper = new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		if (!result.hasErrors()) {
			for(MultipartFile file : fileUploadForm.getFiles()) {
				if (file.getSize() > 0) {
					try {
						SherlockEngine.storage.storeFile(workspaceWrapper.getiWorkspace(), file.getOriginalFilename(), file.getBytes());
					} catch (IOException | WorkspaceUnsupportedException e) {
						e.printStackTrace(); // this is a major issue, we should probably quit here
					}
				}
			}
			result.reject("workspaces_message_uploaded_submission"); //Todo: make message appear using "alert-success" not "alert-warning"
		}

		model.addAttribute("workspace", workspaceWrapper);
		return "dashboard/workspaces/fragments/submissions";
	}

	@GetMapping ("/dashboard/workspaces/manage/jobs/{id}")
	public String jobsGetFragment(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		WorkspaceWrapper workspaceWrapper;
		try {
			workspaceWrapper = new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspaceWrapper);
		model.addAttribute(
				"templates",
				templateRepository.findAccountAndPublic(this.getAccount(authentication))
		);
		model = this.isAjax(model, request);
		return "dashboard/workspaces/fragments/jobs";
	}

	@PostMapping ("/dashboard/workspaces/manage/jobs/{id}")
	public String jobsPostFragment(
			@PathVariable(value="id") long id,
			@RequestParam(value="template_id", required=true) long template_id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {
		WorkspaceWrapper workspaceWrapper;
		try {
			workspaceWrapper = new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		Template template = templateRepository.findByIdAndPublic(template_id, this.getAccount(authentication));
		if (template == null) {
			return "redirect:/dashboard/workspaces?msg=templatenotfound"; //TODO: make into proper error message
		}
		if (template.getDetectors().size() == 0) {
			return "redirect:/dashboard/workspaces?msg=nodetectors"; //TODO: make into proper error message
		}

		IJob job = workspaceWrapper.getiWorkspace().createJob();

		for (TemplateDetector td : template.getDetectors()) {
			try {
				Class<? extends IDetector> detector = (Class<? extends IDetector>) Class.forName(td.getName());
				job.addDetector(detector);
				//TODO: deal with custom parameters
//				job.setParameter(SherlockRegistry.getDetectorAdjustableParameters(detector).get(0), 7);
			} catch (ClassNotFoundException e) {
				e.printStackTrace(); //TODO: deal with error
			}
		}

		job.prepare();

		//SherlockEngine.executor.submitJob(job);
		SherlockEngine.submitToExecutor(job); //temporary timed method for benchmarking, usually the above method would be used!

		model.addAttribute("workspace", workspaceWrapper);
		model.addAttribute(
				"templates",
				templateRepository.findAccountAndPublic(this.getAccount(authentication))
		);
		model = this.isAjax(model, request);
		return "redirect:/dashboard/workspaces/manage/" + workspaceWrapper.getId();
	}

	@GetMapping ("/dashboard/workspaces/manage/results/{id}")
	public String resultsGetFragment(
			@PathVariable(value="id") long id,
			Model model,
			HttpServletRequest request,
			Authentication authentication
	) {

		WorkspaceWrapper workspaceWrapper;
		try {
			workspaceWrapper = new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}
		List<IJob> jobs = workspaceWrapper.getiWorkspace().getJobs();
		model.addAttribute("jobs", jobs);
//		jobs.get(0).getTasks().get(0).getRawResults().get(0).

		model.addAttribute("workspace", workspaceWrapper);
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
		WorkspaceWrapper workspaceWrapper;
		try {
			workspaceWrapper = new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
			return "redirect:/dashboard/workspaces?msg=notfound";
		}

		model.addAttribute("workspace", workspaceWrapper);
		model = this.isAjax(model, request);
		return "dashboard/workspaces/delete";
	}

	@PostMapping ("/dashboard/workspaces/delete/{id}")
	public String deletePost(
			@PathVariable(value="id") long id,
			Authentication authentication
	) {
		try {
			new WorkspaceWrapper(id, this.getAccount(authentication), workspaceRepository);
		} catch (WorkspaceNotFound | IWorkspaceNotFound ex) {
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
