package uk.ac.warwick.dcs.sherlock.module.web.controlleradvice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AttributesControllerAdvice {
    @Autowired
    private AccountRepository accountRepository;

    @ModelAttribute("account")
    public Account getAccount(Authentication authentication)
    {
        if (authentication == null)
            return new Account();

        Account account = accountRepository.findByEmail(authentication.getName());

        return account;
    }

    @ModelAttribute
    public void addIsAjax(Model model, HttpServletRequest request) {
        model.addAttribute("ajax", request.getParameterMap().containsKey("ajax"));
    }

    @ModelAttribute("isAjax")
    public boolean isAjax(HttpServletRequest request) {
        return request.getParameterMap().containsKey("ajax");
    }
}
