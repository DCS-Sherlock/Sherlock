package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TParameter;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TParameterRepository;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.TemplateNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotTemplateOwner;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.TemplateForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TemplateRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * The wrapper that manages the job templates
 */
public class TemplateWrapper {
    /**
     * The job template entity
     */
    private Template template;

    /**
     * Whether or not the current user owns the template
     */
    private boolean isOwner = false;

    /**
     * Initialise the wrapper using the form to create a new template
     *
     * @param templateForm the form to use
     * @param account the account of current user
     * @param templateRepository the database repository
     * @param tDetectorRepository the database repository
     *
     * @throws NotTemplateOwner if the user is not the owner of the template
     */
    public TemplateWrapper(
            TemplateForm templateForm,
            Account account,
            TemplateRepository templateRepository,
            TDetectorRepository tDetectorRepository
    ) throws NotTemplateOwner {
        this.template = new Template();
        this.template.setAccount(account);
        this.isOwner = true;
        this.update(templateForm, templateRepository, tDetectorRepository);
    }

    /**
     * Initialise the template wrapper using an id to find one in the database
     *
     * @param id the id of the template
     * @param account the account of the current user
     * @param templateRepository the database repository
     *
     * @throws TemplateNotFound if the template was not found
     */
    public TemplateWrapper(
            long id,
            Account account,
            TemplateRepository templateRepository
    ) throws TemplateNotFound {
        this.template = templateRepository.findByIdAndPublic(id, account);

        if (this.template == null)
            throw new TemplateNotFound("Template not found.");

        if (this.template.getAccount() == account)
            this.isOwner = true;
    }

    /**
     * Initialise the template wrapper using an existing template
     *
     * @param template the template to manage
     * @param account the account of the current user
     */
    public TemplateWrapper(Template template, Account account)  {
        this.template = template;

        if (this.template.getAccount() == account)
            this.isOwner = true;
    }

    /**
     * Get the template
     *
     * @return the template
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Set the template
     *
     * @param template the new template
     */
    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     *  Whether or not the template is owned by the current user
     *
     * @return the result
     */
    public boolean isOwner() {
        return isOwner;
    }

    /**
     * Update the isOwned property
     *
     * @param owner the new value
     */
    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    /**
     * Get the name of the owner
     *
     * @return the name
     */
    public String getOwnerName() {
        return this.template.getAccount().getUsername();
    }

    /**
     * Whether or not the template is public
     *
     * @return the result
     */
    public boolean isPublic() {
        return this.template.isPublic();
    }

    /**
     * Get the list of detector wrappers active in the template
     *
     * @return the list
     */
    public List<DetectorWrapper> getDetectors() {
        List<DetectorWrapper> wrapperList = new ArrayList<>();
        this.template.getDetectors().forEach(d -> wrapperList.add(new DetectorWrapper(d, this.isOwner)));
        return wrapperList;
    }

    /**
     * Update the template using the form supplied
     *
     * @param templateForm the form to use
     * @param templateRepository the database repository
     * @param templateDetectorRepository the database repository
     *
     * @throws NotTemplateOwner if the user is not the template owner
     */
    public void update(
            TemplateForm templateForm,
            TemplateRepository templateRepository,
            TDetectorRepository templateDetectorRepository
    ) throws NotTemplateOwner {
        if (!this.isOwner)
            throw new NotTemplateOwner("You are not the owner of this template.");

        template.setName(templateForm.getName());
        template.setLanguage(templateForm.getLanguage());
        template.setPublic(templateForm.isPublic());
        templateRepository.save(template);

        List<String> activeDetectors = EngineDetectorWrapper.getDetectorNames(template.getLanguage());

        List<String> toAdd = new ArrayList<>();
        List<String> toRemove = new ArrayList<>();
        List<String> toCheck = new ArrayList<>();

        toAdd.addAll(templateForm.getDetectors());
        template.getDetectors().forEach(d -> toAdd.remove(d.getName()));

        template.getDetectors().forEach(d -> toRemove.add(d.getName()));
        toRemove.removeAll(templateForm.getDetectors());

        for (String add : toAdd) {
            templateDetectorRepository.save(new TDetector(add, template));
        }

        for (String remove : toRemove) {
            templateDetectorRepository.delete(
                    templateDetectorRepository.findByNameAndTemplate(remove, template)
            );
        }

        toCheck.addAll(toAdd);
        template.getDetectors().forEach(d -> toCheck.add(d.getName()));
        toCheck.removeAll(toRemove);

        for (String check : toCheck) {
            if (!activeDetectors.contains(check)) {
                templateDetectorRepository.delete(
                        templateDetectorRepository.findByNameAndTemplate(check, template)
                );
            }
        }
    }

    /**
     * Make a copy the template
     *
     * @param account the account of the current user
     * @param templateRepository the database repository
     * @param tDetectorRepository the database repository
     * @param tParameterRepository the database repository
     *
     * @return the new template
     */
    public Template copy(
            AccountWrapper account,
            TemplateRepository templateRepository,
            TDetectorRepository tDetectorRepository,
            TParameterRepository tParameterRepository
    ) {
        Template template = new Template();
        template.setAccount(account.getAccount());
        template.setLanguage(this.template.getLanguage());
        template.setPublic(false);
        template.setName(this.template.getName() + " - Copy");
        templateRepository.save(template);

        for (TDetector detector : this.template.getDetectors()) {
            TDetector newDetector = new TDetector();
            newDetector.setName(detector.getName());
            newDetector.setTemplate(template);

            tDetectorRepository.save(newDetector);

            for (TParameter parameter : detector.getParameters()) {
                TParameter newParameter = new TParameter();
                newParameter.setName(parameter.getName());
                newParameter.setValue(parameter.getValue());
                newParameter.setDetector(newDetector);

                tParameterRepository.save(newParameter);
            }
        }

        return template;
    }

    /**
     * Delete the template
     *
     * @param templateRepository the database repository
     *
     * @throws NotTemplateOwner if the user is not the template owner
     */
    public void delete(TemplateRepository templateRepository) throws NotTemplateOwner {
        if (!this.isOwner)
            throw new NotTemplateOwner("You are not the owner of this template.");

        templateRepository.delete(this.template);
    }

    /**
     * Get the list of templates that are public or owned by the user
     *
     * @param account the account of the current user
     * @param templateRepository the database repository
     *
     * @return the list of templates
     */
    public static List<TemplateWrapper> findByAccountAndPublic(Account account, TemplateRepository templateRepository) {
        List<TemplateWrapper> wrapperList = new ArrayList<>();
        List<Template> templateList = templateRepository.findByAccountAndPublic(account);
        templateList.forEach(t -> wrapperList.add(new TemplateWrapper(t, account)));
        return wrapperList;
    }

    /**
     * Get the list of templates that are public, owned by the user and filter
     * by the language supplied
     *
     * @param account the account of the current user
     * @param templateRepository the database repository
     * @param language the language to filter by
     *
     * @return the list of templates
     */
    public static List<TemplateWrapper> findByAccountAndPublicAndLanguage(Account account, TemplateRepository templateRepository, String language) {
        List<TemplateWrapper> wrapperList = new ArrayList<>();
        List<Template> templateList = templateRepository.findByAccountAndPublicAndLanguage(account, language);
        templateList.forEach(t -> wrapperList.add(new TemplateWrapper(t, account)));
        return wrapperList;
    }
}
