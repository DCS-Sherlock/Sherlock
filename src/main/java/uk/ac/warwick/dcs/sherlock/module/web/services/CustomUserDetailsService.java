package uk.ac.warwick.dcs.sherlock.module.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Role;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.AccountRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * The custom user details service which fetches the account and
 * their roles from the database from the authentication session
 * information
 */
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    //All @Autowired variables are automatically loaded by Spring
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Searches for the account using the email, loads the roles associated
     * to that account. It then creates a "user" object using the email,
     * encoded password and roles for the authentication manager
     *
     * @param email the email of the user to find
     *
     * @return the new "user" object
     *
     * @throws UsernameNotFoundException if the email was not found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("Account not found.");
        }

        Set<GrantedAuthority> roles = new HashSet<>();
        for (Role role : account.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new User(account.getEmail(), account.getPassword(), roles);
    }
}
