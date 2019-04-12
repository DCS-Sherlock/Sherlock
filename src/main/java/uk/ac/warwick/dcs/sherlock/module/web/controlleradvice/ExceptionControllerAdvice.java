package uk.ac.warwick.dcs.sherlock.module.web.controlleradvice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;

import java.util.Arrays;

/**
 * Handles exceptions thrown by all of the controllers
 */
@ControllerAdvice
public class ExceptionControllerAdvice {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private Environment environment;

    @ExceptionHandler({SpringNotInitialised.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String notInitialised(Model model, Exception e) {
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }

    /**
     * Handles requests which are only allowed to be ajax requests. Redirects
     * the user to the url supplied in the error message, typically this is
     * the page that contained the form/page element
     *
     * @param e the exception object
     *
     * @return the page to redirect to
     */
    @ExceptionHandler({NotAjaxRequest.class})
    public String notAjaxRequest(NotAjaxRequest e) {
        return "redirect:" + e.getMessage() + "?msg=ajax";
    }

    /**
     * Handles all generic/unknown errors
     *
     * @param model holder for model attributes
     * @param e the exception object
     *
     * @return the path to the error page
     */
    @ExceptionHandler({
            Throwable.class,
            LoadingHelpFailed.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String runtimeError(Model model, Exception e) {
        //If running in dev mode, print the error
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            e.printStackTrace();
        }

        return "error";
    }

    /**
     * Handles all "not found" errors
     *
     * @param model holder for model attributes
     * @param authentication the authentication class
     * @param e the exception object
     *
     * @return the path to the error page
     */
    @ExceptionHandler({
            IWorkspaceNotFound.class,
            WorkspaceNotFound.class,
            TemplateNotFound.class,
            SubmissionNotFound.class,
            DetectorNotFound.class,
            ResultsNotFound.class,
            SourceFileNotFound.class,
            AccountNotFound.class,
            CompareSameSubmission.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFoundError(Model model, Authentication authentication, Exception e) {
        model = addAccountToModel(model, authentication);
        model.addAttribute("msg", e.getClass().getName());
        return "error-default";
    }

    /**
     * Handles all requests which are not authorised
     *
     * @param model holder for model attributes
     * @param authentication the authentication class
     * @param e the exception object
     *
     * @return the path to the error page
     */
    @ExceptionHandler({
            NotTemplateOwner.class,
            AccountOwner.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String notAuthorisedError(Model model, Authentication authentication, Exception e) {
        model = addAccountToModel(model, authentication);
        model.addAttribute("msg", e.getClass().getName());
        return "error-default";
    }

    /**
     * Handles all requests where the user tried to upload files that are larger than allowed
     *
     * @param model holder for model attributes
     * @param authentication the authentication class
     * @param e the exception object
     *
     * @return the path to the error page
     */
    @ExceptionHandler({
            MaxUploadSizeExceededException.class
    })
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String uploadSizeExceededError(Model model, Authentication authentication, Exception e) {
        model = addAccountToModel(model, authentication);
        model.addAttribute("msg", e.getClass().getName());
        return "error-default";
    }

    /**
     * Adds the account object of the current user to the display model
     *
     * @param model holder for model attributes
     * @param authentication the authentication class
     *
     * @return the model updated with the account object
     */
    private Model addAccountToModel(Model model, Authentication authentication) {
        AccountWrapper accountWrapper = new AccountWrapper();
        if (authentication != null)
            accountWrapper = new AccountWrapper(accountRepository.findByEmail(authentication.getName()));
        model.addAttribute("account", accountWrapper);
        return model;
    }
}
