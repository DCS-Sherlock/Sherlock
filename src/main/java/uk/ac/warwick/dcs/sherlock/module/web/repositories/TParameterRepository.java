package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TParameter;

import java.util.List;

public interface TParameterRepository extends JpaRepository<TParameter, Long> {
    List<TParameter> findByTDetector(TDetector tDetector);
}