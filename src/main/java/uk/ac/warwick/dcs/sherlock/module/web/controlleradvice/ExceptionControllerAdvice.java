package uk.ac.warwick.dcs.sherlock.module.web.controlleradvice;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler({NotAjaxRequest.class})
    public String notAjaxRequest(NotAjaxRequest e) {
        return "redirect:" + e.getMessage() + "?msg=ajax";
    }

    @ExceptionHandler({
            NotTemplateOwner.class
    })
    public String genericError(Model model, Exception e) {
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String runtimeError(Model model, Exception e) {
        e.printStackTrace(); //TODO make dev only
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }

    @ExceptionHandler({
            IWorkspaceNotFound.class,
            WorkspaceNotFound.class,
            TemplateNotFound.class,
            SourceFileNotFound.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFoundError(Model model, Exception e) {
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }

}
