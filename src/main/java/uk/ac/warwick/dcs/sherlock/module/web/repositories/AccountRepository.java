package uk.ac.warwick.dcs.sherlock.module.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;

/**
 * The database repository storing the account details
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmail(String email);
}