package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TDetector;

public interface TDetectorRepository extends JpaRepository<TDetector, Long> {
    TDetector findByNameAndTemplate(String name, Template template);
}