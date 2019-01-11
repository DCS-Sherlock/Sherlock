package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Workspace;

import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByAccount(Account account);
    Workspace findByIdAndAccount(long id, Account account);
}