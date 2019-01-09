package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmail(String email);
}