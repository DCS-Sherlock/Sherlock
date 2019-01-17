package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Template;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    @Query("SELECT t FROM Template t WHERE t.id = :id AND (t.account = :account OR t.isPublic = true)")
    Template findByIdAndPublic(@Param("id")long id, @Param("account")Account account);

    @Query("SELECT t FROM Template t WHERE t.account = :account OR t.isPublic = true")
    List<Template> findByAccountAndPublic(@Param("account") Account account);
}