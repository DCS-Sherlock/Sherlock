package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Workspace;

import java.util.List;

/**
 * The database repository storing all workspaces
 */
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    /**
     * Finds all workspaces owned by the supplied account
     *
     * @param account the account to filter by
     *
     * @return the list of workspaces found
     */
    List<Workspace> findByAccount(Account account);

    /**
     * Finds a workspace with the supplied id and owned by the
     * supplied account
     *
     * @param id the if of the workspace to find
     * @param account the account to filter by
     *
     * @return the workspace found
     */
    Workspace findByIdAndAccount(long id, Account account);
}