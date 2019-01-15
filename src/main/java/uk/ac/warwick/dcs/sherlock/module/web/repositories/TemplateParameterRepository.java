package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TemplateParameter;

public interface TemplateParameterRepository extends JpaRepository<TemplateParameter, Long> { }