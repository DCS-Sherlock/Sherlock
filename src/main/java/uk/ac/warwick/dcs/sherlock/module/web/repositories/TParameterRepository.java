package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TParameter;

import java.util.List;

/**
 * The database repository that stores the parameters for each detector
 */
public interface TParameterRepository extends JpaRepository<TParameter, Long> {
    /**
     * Finds all the parameters that are linked to the supplied detector
     *
     * @param tDetector the detector to filter by
     *
     * @return the list of parameters
     */
    List<TParameter> findByTDetector(TDetector tDetector);
}