package uk.ac.warwick.dcs.sherlock.module.web.validation.validators;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidLanguage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidLanguageValidator implements ConstraintValidator<ValidLanguage, String> {
    public ValidLanguageValidator() { }

    public void initialize(ValidLanguage constraint) { }

    public boolean isValid(String language, ConstraintValidatorContext context) {
        return language != null && SherlockRegistry.getLanguages().contains(language);
    }
}
