package uk.ac.warwick.dcs.sherlock.module.web.controlleradvice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;

import javax.servlet.http.HttpServletRequest;

/**
 * Declares ModelAttributes for all controllers
 */
@ControllerAdvice
public class AttributesControllerAdvice {
    //All @Autowired variables are automatically loaded by Spring
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Gets the account of the currently logged in user using the authentication
     * details and adds it to the model attributes
     *
     * @param model holder for model attributes (auto-filled by Spring)
     * @param authentication the authentication class (auto-filled by Spring)
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
     * Adds an "is ajax" boolean to the attributes of all requests
     *
     * @param model holder for model attributes (auto-filled by Spring)
     * @param request the http request information (auto-filled by Spring)
     */
    @ModelAttribute
    public void addIsAjax(Model model, HttpServletRequest request) {
        model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
    }

    /**
     * Checks whether the current request is ajax or not
     *
     * @param request the http request information (auto-filled by Spring)
     *
     * @return whether or not the request is an ajax one
     */
    @ModelAttribute("isAjax")
    public boolean isAjax(HttpServletRequest request) {
        return request.getParameterMap().containsKey("ajax");
    }
}
