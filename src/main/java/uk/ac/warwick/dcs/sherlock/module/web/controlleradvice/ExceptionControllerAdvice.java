package uk.ac.warwick.dcs.sherlock.module.web.controlleradvice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;

import java.util.Arrays;

/**
 * Handles exceptions thrown by all of the controllers
 */
@ControllerAdvice
public class ExceptionControllerAdvice {
    //All @Autowired variables are automatically loaded by Spring
    @Autowired
    private Environment environment;

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
     * @param model holder for model attributes (auto-filled by Spring)
     *
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

//        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }

    /**
     * Handles all "not found" errors
     *
     * @param model holder for model attributes (auto-filled by Spring)
     *
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
            SourceFileNotFound.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFoundError(Model model, Exception e) {
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }

    /**
     * Handles all requests which are not authorised
     *
     * @param model holder for model attributes (auto-filled by Spring)
     *
     * @param e the exception object
     *
     * @return the path to the error page
     */
    @ExceptionHandler({
            NotTemplateOwner.class,
            AccountOwner.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String notAuthorisedError(Model model, Exception e) {
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }

    @ExceptionHandler({
            MaxUploadSizeExceededException.class
    })
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String uploadSizeExceededError(Model model, Exception e) {
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }
}
