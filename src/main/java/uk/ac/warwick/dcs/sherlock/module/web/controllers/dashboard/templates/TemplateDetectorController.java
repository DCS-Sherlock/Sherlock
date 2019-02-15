package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.DetectorNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.ParameterForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.DetectorWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TParameterRepository;

import javax.validation.Valid;

@Controller
public class TemplateDetectorController {
    @Autowired
    private TDetectorRepository tDetectorRepository;
    @Autowired
	private TParameterRepository tParameterRepository;

    public TemplateDetectorController() { }

	@GetMapping("/dashboard/templates/manage/detectors/parameters/{pathid}")
	public String parmetersGet(
			@ModelAttribute("detector") DetectorWrapper detectorWrapper,
			Model model
	) throws DetectorNotFound {
		model.addAttribute("parameterForm", new ParameterForm(detectorWrapper));
		model.addAttribute("parametersMap", detectorWrapper.getEngineParametersMap());
		return "dashboard/templates/parameters";
	}

	@PostMapping("/dashboard/templates/manage/detectors/parameters/{pathid}")
	public String parametersPost(
			@Valid @ModelAttribute ParameterForm parameterForm,
			BindingResult result,
			@ModelAttribute("detector") DetectorWrapper detectorWrapper,
			Model model
	) throws DetectorNotFound {
    	result = parameterForm.validate(result, detectorWrapper.getEngineParameters());

		if (!result.hasErrors()) {
			detectorWrapper.updateParameters(parameterForm, tParameterRepository);
			model.addAttribute("success_msg", "templates_parameters_updated_msg");
		}

		model.addAttribute("parametersMap", detectorWrapper.getEngineParametersMap());
		return "dashboard/templates/parameters";
	}

	@ModelAttribute("detector")
	private DetectorWrapper getDetectorWrapper(
            @ModelAttribute("account") AccountWrapper account,
            @PathVariable(value="pathid") long pathid,
            Model model)
        throws DetectorNotFound
    {
        DetectorWrapper detectorWrapper = new DetectorWrapper(pathid, account.getAccount(), tDetectorRepository);
        model.addAttribute("detector", detectorWrapper);
		return detectorWrapper;
	}
}