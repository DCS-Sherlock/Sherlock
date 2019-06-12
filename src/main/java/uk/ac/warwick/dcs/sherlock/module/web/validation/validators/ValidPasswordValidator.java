package uk.ac.warwick.dcs.sherlock.module.web.validation.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Form validator that checks if the password supplied matches that
 * of the current user
 */
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {
    //All @Autowired variables are automatically loaded by Spring
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public ValidPasswordValidator() { }

    public void initialize(ValidPassword constraint) { }

    /**
     * Performs the validation step by fetching the account from the
     * repository, encoding the supplied password and checking that
     * against the encoded password stored in the database
     *
     * @param password the plaintext password from the form
     * @param context (not used here)
     *
     * @return whether or not the validation passed
     */
    public boolean isValid(String password, ConstraintValidatorContext context) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        Account account = accountRepository.findByEmail(authentication.getName());

        if (passwordEncoder.matches(password, account.getPassword())) {
            return true;
        }

        return false;
    }
}
