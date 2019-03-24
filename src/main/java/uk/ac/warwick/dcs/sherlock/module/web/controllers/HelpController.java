package uk.ac.warwick.dcs.sherlock.module.web.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContextUtils;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.LoadingHelpFailed;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The controller that deals with the help pages (help centre, terms and privacy policy)
 */
@Controller
public class HelpController {
	/**
	 * Handles requests to the help page
	 *
	 * @param model holder for model attributes (auto-filled by Spring)
	 * @param request the http request information (auto-filled by Spring)
	 *
	 * @return the path to the help page
	 *
	 * @throws LoadingHelpFailed if the help.properties file for both the current
	 * locale and default failed to load
	 */
	@RequestMapping ("/help")
	public String index(Model model, HttpServletRequest request) throws LoadingHelpFailed {
		String locale = RequestContextUtils.getLocale(request).toLanguageTag();

		Properties properties;
		try {
			properties = this.loadProperties(locale);
		} catch (LoadingHelpFailed e) {
			properties = this.loadProperties("");
		}

		Map<String, String> questions = new HashMap<>();
		for (String s : properties.stringPropertyNames()) {
			if (!s.endsWith("_answer")) {
				questions.put(
					properties.getProperty(s, ""),
					properties.getProperty(s+"_answer", "")
				);
			}
		}

		model.addAttribute("questions", questions);
		return "help/index";
	}

	/**
	 * Handles requests to the terms page
	 *
	 * @return the path to the terms page
	 */
	@RequestMapping ("/terms")
	public String terms() {
		return "help/terms";
	}

	/**
	 * Handles requests to the privacy page
	 *
	 * @return the path to the privacy page
	 */
	@RequestMapping ("/privacy")
	public String privacy() {
		return "help/privacy";
	}

	/**
	 * Loads the help.properties file for the supplied locale
	 *
	 * @param locale the locale to load the properties file for
	 *
	 * @return the properties object
	 *
	 * @throws LoadingHelpFailed if the help.properties file for locale failed to load
	 */
	private Properties loadProperties(String locale) throws LoadingHelpFailed {
		if (locale.length() > 0) {
			locale = "_" + locale;
		}

		Resource resource = new ClassPathResource("/help" + locale + ".properties");

		try {
			return PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			throw new LoadingHelpFailed("Loading help.properties file failed.");
		}
	}
}
