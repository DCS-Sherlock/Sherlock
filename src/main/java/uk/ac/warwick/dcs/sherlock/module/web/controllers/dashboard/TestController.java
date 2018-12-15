package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;
import uk.ac.warwick.dcs.sherlock.api.model.data.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.model.TestResultsFactory;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.TestModel;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class TestController {

	private static Logger logger = LoggerFactory.getLogger(TestController.class);

	public TestController() {
		EventBus.registerEventSubscriber(this);
	}

	/*@PostMapping ("/dashboard/test")
	public String handleFileUpload(@RequestParam ("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		if (file.getSize() == 0) {
			logger.info("Blank file uploaded - ignoring");
			return "redirect:/dashboard/test";
		}
		try {
			logger.info(file.getContentType());
			SherlockEngine.storage.storeFile(file.getOriginalFilename(), file.getBytes());
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/dashboard/test";
	}*/

	@GetMapping ("/dashboard/test")
	public String indexForm(Model model) {
		model.addAttribute("form", new TestModel());
		return "dashboard/test";
	}

	@PostMapping ("/dashboard/test")
	public String indexSubmit(@ModelAttribute ("form") TestModel form, Model model) {
		String result = "";

		Path path1 = Paths.get(form.getFilename1());
		Path path2 = Paths.get(form.getFilename2());
		Path path3 = Paths.get(form.getFilename1());
		Path path4 = Paths.get(form.getFilename2());

		if (Files.isRegularFile(path1) && Files.isRegularFile(path2)) {
			try {
				List<ISourceFile> fileList = Collections.synchronizedList(Arrays.asList(new TestResultsFactory.tmpFile(form.getFilename1()), new TestResultsFactory.tmpFile(form.getFilename2()), new TestResultsFactory.tmpFile(form.getFilename1()), new TestResultsFactory.tmpFile(form.getFilename2())));
				result = TestResultsFactory.buildTestResults(fileList, TestDetector.class);
			}
			catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		else {
			result = "Not valid files";
		}

		System.out.println(result);
		model.addAttribute("result", result);

		return "dashboard/testresult";
	}

}