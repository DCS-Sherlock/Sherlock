package uk.ac.warwick.dcs.sherlock.module.web.controlleradvice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler({NotAjaxRequest.class})
    public String notAjaxRequest(NotAjaxRequest e) {
        return "redirect:" + e.getMessage() + "?msg=ajax";
    }

    @ExceptionHandler({
            IWorkspaceNotFound.class,
            WorkspaceNotFound.class,
            TemplateNotFound.class,
            NotTemplateOwner.class
    })
    public String genericError(Model model, Exception e) {
        model.addAttribute("msg", e.getClass().getName());
        return "error";
    }
}
