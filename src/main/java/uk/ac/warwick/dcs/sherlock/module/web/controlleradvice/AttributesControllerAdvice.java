package uk.ac.warwick.dcs.sherlock.module.web.controlleradvice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.launch.SherlockServer;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.SpringNotInitialised;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Declares ModelAttributes for all controllers
 */
@ControllerAdvice
public class AttributesControllerAdvice {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private Environment environment;

    /**
     * Checks that the Spring server has finished initialising and throws an error
     * if a user attempts to load a page before it has finished
     *
     * @throws SpringNotInitialised if the server is still starting up
     */
    @ModelAttribute
    public void checkLoaded() throws SpringNotInitialised {
        if (!SherlockServer.engine.isInitialised()) {
            throw new SpringNotInitialised("Not loaded");
        }
    }

    /**
     * Gets the account of the currently logged in user using the authentication
     * details and adds it to the model attributes
     *
     * @param model holder for model attributes
     * @param authentication the authentication class
     *
     * @return the account wrapper if logged in, or an empty wrapper if not
     */
    @ModelAttribute("account")
    public AccountWrapper getAccount(Model model, Authentication authentication)
    {
        if (authentication == null)
            return new AccountWrapper();

        AccountWrapper accountWrapper = new AccountWrapper(accountRepository.findByEmail(authentication.getName()));

        model.addAttribute("account", accountWrapper);
        return accountWrapper;
    }

    /**
     * Adds the msg parameter to the attributes of all requests
     *
     * @param model holder for model attributes
     * @param request the http request information
     */
    @ModelAttribute
    public void addMessage(Model model, HttpServletRequest request) {
        if (request.getParameterMap().containsKey("msg")) {
            model.addAttribute("top_message", request.getParameterMap().get("msg"));
        } else {
            model.addAttribute("top_message", "");
        }
    }

    /**
     * Adds an "is ajax" boolean to the attributes of all requests
     *
     * @param model holder for model attributes
     * @param request the http request information
     */
    @ModelAttribute
    public void addIsAjax(Model model, HttpServletRequest request) {
        model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
    }

    /**
     * Adds an "is printing" boolean to the attribute of all requests
     *
     * @param model holder for model attributes
     * @param request the http request information
     */
    @ModelAttribute
    public void addIsPrinting(Model model, HttpServletRequest request) {
        model.addAttribute("printing", request.getParameterMap().containsKey("print"));
    }

    /**
     * Checks whether or not the request is attempting to print
     *
     * @param model holder for model attributes
     * @param request the http request information
     *
     * @return whether or not the request is attempting to print
     */
    @ModelAttribute("isPrinting")
    public boolean isPrinting(Model model, HttpServletRequest request) {
        return request.getParameterMap().containsKey("print");
    }


    /**
     * Checks whether the current request is ajax or not
     *
     * @param request the http request information
     *
     * @return whether or not the request is an ajax one
     */
    @ModelAttribute("isAjax")
    public boolean isAjax(HttpServletRequest request) {
        return request.getParameterMap().containsKey("ajax");
    }

    /**
     * Sets the javascript url to the standard version when running the
     * webdev profile and the minified in all other cases
     *
     * @param model holder for model attributes
     */
    @ModelAttribute
    public void addJsUrl(Model model) {
        if (Arrays.asList(environment.getActiveProfiles()).contains("webdev")) {
            model.addAttribute("javascript", "default.js");
        } else {
            model.addAttribute("javascript", "default.min.js");
        }
    }
}
