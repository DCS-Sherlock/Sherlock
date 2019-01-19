package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import uk.ac.warwick.dcs.sherlock.module.web.exceptions.TemplateNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotTemplateOwner;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.TemplateForm;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TemplateRepository;

import java.util.ArrayList;
import java.util.List;

public class TemplateWrapper {
    private Template template;
    private boolean isOwner = false;

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

    public TemplateWrapper(Template template, Account account)  {
        this.template = template;

        if (this.template.getAccount() == account)
            this.isOwner = true;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public List<DetectorWrapper> getDetectors() {
        List<DetectorWrapper> wrapperList = new ArrayList<>();
        this.template.getDetectors().forEach(d -> wrapperList.add(new DetectorWrapper(d, this.isOwner)));
        return wrapperList;
    }

    public static List<TemplateWrapper> findByAccountAndPublic(Account account, TemplateRepository templateRepository) {
        List<TemplateWrapper> wrapperList = new ArrayList<>();
        List<Template> templateList = templateRepository.findByAccountAndPublic(account);
        templateList.forEach(t -> wrapperList.add(new TemplateWrapper(t, account)));
        return wrapperList;
    }

    public static List<TemplateWrapper> findByAccountAndPublicAndLanguage(Account account, TemplateRepository templateRepository, String language) {
        List<TemplateWrapper> wrapperList = new ArrayList<>();
        List<Template> templateList = templateRepository.findByAccountAndPublicAndLanguage(account, language);
        templateList.forEach(t -> wrapperList.add(new TemplateWrapper(t, account)));
        return wrapperList;
    }

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
                //TODO: detector no longer exists or not supported by the language, should return an error
            }
        }
    }

    public void delete(TemplateRepository templateRepository) throws NotTemplateOwner {
        if (!this.isOwner)
            throw new NotTemplateOwner("You are not the owner of this template.");

        templateRepository.delete(this.template);
    }
}
