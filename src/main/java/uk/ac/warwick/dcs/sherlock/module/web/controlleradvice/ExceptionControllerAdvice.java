package uk.ac.warwick.dcs.sherlock.module.web.controlleradvice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;

import java.util.Arrays;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @Autowired
    private Environment environment;

    @ExceptionHandler({NotAjaxRequest.class})
    public String notAjaxRequest(NotAjaxRequest e) {
        return "redirect:" + e.getMessage() + "?msg=ajax";
    }

    @ExceptionHandler({
            Throwable.class,
            LoadingHelpFailed.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String runtimeError(Model model, Exception e) {
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            e.printStackTrace();
        }

        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }

    @ExceptionHandler({
            IWorkspaceNotFound.class,
            WorkspaceNotFound.class,
            TemplateNotFound.class,
            SourceFileNotFound.class,
            DetectorNotFound.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFoundError(Model model, Exception e) {
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }

    @ExceptionHandler({
            NotTemplateOwner.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String notAuthorisedError(Model model, Exception e) {
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }
}
