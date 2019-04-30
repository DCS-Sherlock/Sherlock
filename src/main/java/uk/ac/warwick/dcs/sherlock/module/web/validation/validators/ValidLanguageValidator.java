package uk.ac.warwick.dcs.sherlock.module.web.validation.validators;

import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidLanguage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Form validator that checks if the language supplied is
 * valid (i.e. in the registry of supported languages)
 */
public class ValidLanguageValidator implements ConstraintValidator<ValidLanguage, String> {
    public ValidLanguageValidator() { }

    public void initialize(ValidLanguage constraint) { }

    /**
     * Performs the validation step by checking that the language
     * is set and then that it is in the list of languages in the
     * Sherlock Registry
     *
     * @param language the language to check
     * @param context (not used here)
     *
     * @return whether or not the validation passed
     */
    public boolean isValid(String language, ConstraintValidatorContext context) {
        return language != null && SherlockRegistry.getLanguages().contains(language);
    }
}
