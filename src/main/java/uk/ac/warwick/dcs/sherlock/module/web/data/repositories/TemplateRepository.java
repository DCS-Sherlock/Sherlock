package uk.ac.warwick.dcs.sherlock.module.web.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Template;

import java.util.List;

/**
 * The database repository storing the job templates
 */
public interface TemplateRepository extends JpaRepository<Template, Long> {
    /**
     * Finds the template with the id supplied only if it is
     * owned by the account supplied or is public
     *
     * @param id the id of the template to find
     * @param account the account object of the current user
     *
     * @return the template found
     */
    @Query("SELECT t FROM Template t WHERE t.id = :id AND (t.account = :account OR t.isPublic = true)")
    Template findByIdAndPublic(@Param("id")long id, @Param("account")Account account);

    /**
     * Finds all templates that are owned by the account or
     * are public
     *
     * @param account the account object of the current user
     *
     * @return the list of templates found
     */
    @Query("SELECT t FROM Template t WHERE t.account = :account OR t.isPublic = true")
    List<Template> findByAccountAndPublic(@Param("account") Account account);


    /**
     * Finds all templates of a specific language that are
     * owned by the account or are public
     *
     * @param account the account object of the current user
     * @param language the language to filter by
     *
     * @return the list of templates found
     */
    @Query("SELECT t FROM Template t WHERE t.language = :language AND (t.account = :account OR t.isPublic = true)")
    List<Template> findByAccountAndPublicAndLanguage(@Param("account") Account account, @Param("language") String language);
}