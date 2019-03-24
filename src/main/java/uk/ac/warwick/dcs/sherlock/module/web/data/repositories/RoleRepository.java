package uk.ac.warwick.dcs.sherlock.module.web.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Role;

/**
 * The database repository storing the roles for each accound
 */
public interface RoleRepository extends JpaRepository<Role, String> { }