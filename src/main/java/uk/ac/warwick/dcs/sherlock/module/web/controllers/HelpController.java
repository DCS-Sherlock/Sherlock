package uk.ac.warwick.dcs.sherlock.module.web.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Controller
public class HelpController {
	public HelpController() { }

	//TODO: Add locale support
	@RequestMapping ("/help")
	public String index(Model model) {
		Resource resource = new ClassPathResource("/help.properties");
		Properties properties = null;
		Map<String, String> questions = new HashMap<>();

		try {
			properties = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace(); //TODO: deal with error
		}

		if (properties != null) {
			for (String s : properties.stringPropertyNames()) {
				if (!s.endsWith("_answer")) {
					questions.put(
							properties.getProperty(s, ""),
							properties.getProperty(s+"_answer", "")
					);
				}
			}
		}

		model.addAttribute("questions", questions);
		return "help/index";
	}

	@RequestMapping ("/terms")
	public String terms() {
		return "help/terms";
	}

	@RequestMapping ("/privacy")
	public String privacy() {
		return "help/privacy";
	}

}
