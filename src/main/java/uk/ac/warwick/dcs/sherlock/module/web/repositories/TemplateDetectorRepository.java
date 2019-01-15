package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TemplateDetector;

import java.util.List;

public interface TemplateDetectorRepository extends JpaRepository<TemplateDetector, Long> {
    TemplateDetector findByNameAndTemplate(String name, Template template);
    List<TemplateDetector> findByTemplate(Template template);
}