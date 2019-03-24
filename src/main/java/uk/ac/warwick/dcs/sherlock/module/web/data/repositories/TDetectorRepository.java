package uk.ac.warwick.dcs.sherlock.module.web.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TDetector;

/**
 * The database repository storing the detectors for each job template
 */
public interface TDetectorRepository extends JpaRepository<TDetector, Long> {
    /**
     * Finds the detector with the supplied name that is linked to the
     * supplied template
     *
     * @param name the name of the detector to find
     * @param template the template the detector is linked to
     *
     * @return the detector found
     */
    TDetector findByNameAndTemplate(String name, Template template);
}