package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import org.hibernate.Hibernate;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TemplateDetector;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.EngineDetectorWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TemplateDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TemplateRepository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class TemplateForm {

    @NotNull(message = "{error_name_empty}")
    @Size.List({
            @Size(
                    min = 1,
                    message = "{error_name_empty}"),
            @Size(
                    max = 64,
                    message = "{error_name_length_max}")
    })
    public String name;

    @NotNull(message = "{error_language_empty}")
    public Language language;

    public boolean isPublic;

    public List<String> detectors = new ArrayList<>();

    public TemplateForm() { }

    public TemplateForm(Template template) {
        this.name = template.getName();
        this.language = template.getLanguage();
        this.isPublic = template.isPublic();
        template.getDetectors().forEach(d -> this.detectors.add(d.getName()));
    }

    public TemplateForm(Language language) {
        this.language = language;
    }

    public TemplateForm(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<String> getDetectors() {
        return detectors;
    }

    public void setDetectors(List<String> detectors) {
        this.detectors = detectors;
    }

    public void updateTemplate(Template template,
                               TemplateRepository templateRepository,
                               TemplateDetectorRepository templateDetectorRepository) {
        template.setName(this.name);
        template.setLanguage(this.language);
        template.setPublic(this.isPublic);
        templateRepository.save(template);

        List<String> activeDetectors = EngineDetectorWrapper.getDetectorNames(this.language);

        List<String> toAdd = new ArrayList<>();
        List<String> toRemove = new ArrayList<>();
        List<String> toCheck = new ArrayList<>();

        toAdd.addAll(this.detectors);
        toAdd.removeAll(template.getDetectors());

        template.getDetectors().forEach(d -> toRemove.add(d.getName()));
        toRemove.removeAll(this.detectors);

        for (String add : toAdd) {
            templateDetectorRepository.save(new TemplateDetector(add, template));
        }

        for (String remove : toRemove) {
            templateDetectorRepository.delete(
                    templateDetectorRepository.findByNameAndTemplate(remove, template)
            );
        }

        template.getDetectors().forEach(d -> toCheck.add(d.getName()));

        for (String check : toCheck) {
            if (!activeDetectors.contains(check)) {
                templateDetectorRepository.delete(
                        templateDetectorRepository.findByNameAndTemplate(check, template)
                );
                //TODO: detector no longer exists or not supported by the language, should return an error
            }
        }
    }
}
