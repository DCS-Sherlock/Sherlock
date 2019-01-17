package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.EngineDetectorWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.TemplateWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TDetectorRepository;
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

    public TemplateForm(Language language) {
        this.language = language;
    }

    public TemplateForm(TemplateWrapper templateWrapper) {
        this.name = templateWrapper.getTemplate().getName();
        this.language = templateWrapper.getTemplate().getLanguage();
        this.isPublic = templateWrapper.getTemplate().isPublic();
        templateWrapper.getTemplate().getDetectors().forEach(d -> this.detectors.add(d.getName()));
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
}
