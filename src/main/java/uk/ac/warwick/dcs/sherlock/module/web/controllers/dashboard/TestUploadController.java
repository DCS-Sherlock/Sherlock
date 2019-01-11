package uk.ac.warwick.dcs.sherlock.module.web.controllers.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.warwick.dcs.sherlock.api.event.EventBus;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

import java.io.IOException;

@Controller
public class TestUploadController {

	private static Logger logger = LoggerFactory.getLogger(TestUploadController.class);

	public TestUploadController() {
		EventBus.registerEventSubscriber(this);
	}

	@PostMapping ("/dashboard/testUpload")
	public String handleFileUpload(@RequestParam ("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		if (file.getSize() == 0) {
			logger.info("Blank file uploaded - ignoring");
			return "redirect:/dashboard/testUpload";
		}
		// rewrite to get a workspace so we can call storeFile
		/*try {
			logger.info(file.getContentType());
			SherlockEngine.storage.storeFile(file.getOriginalFilename(), file.getBytes());
		}
		catch (IOException e) {
			e.printStackTrace();
		}*/

		redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/dashboard/testUpload";
	}

	@GetMapping ("/dashboard/testUpload")
	public String indexForm(Model model) {
		return "dashboard/testUpload";
	}

}