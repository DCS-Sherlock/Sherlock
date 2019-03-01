package uk.ac.warwick.dcs.sherlock.module.web.configuration;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Role;
import uk.ac.warwick.dcs.sherlock.module.web.configuration.properties.SecurityProperties;
import uk.ac.warwick.dcs.sherlock.module.web.configuration.properties.SetupProperties;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.RoleRepository;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * Sets up both the web and http security to prevent unauthorised access to account/admin pages
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	//All @Autowired variables are automatically loaded by Spring
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private Environment environment;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private SecurityProperties securityProperties;
	@Autowired
	private SetupProperties setupProperties;
	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * Create a password encoder bean that uses the BCrypt strong hashing function
	 *
	 * @return the PasswordEncoder implementation
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	};

	/**
	 * Configures the web security by configuring the authentication manager to
	 * use the custom user details service that links into the datbase
	 *
	 * @param auth Spring's authentication manager
	 *
	 * @throws Exception
	 */
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		//Check if running as a client
		if (Arrays.asList(environment.getActiveProfiles()).contains("client")) {
			//Try to find the "local user"
			String email = "local.sherlock@example.com"; //TODO: don't make hardcoded
			Account account = accountRepository.findByEmail(email);

			//Add the "local user" if not found
			if (account == null) {
				account = new Account(email, bCryptPasswordEncoder.encode("local_password"), "Local User");
				accountRepository.save(account);
				roleRepository.save(new Role("USER", account));
				roleRepository.save(new Role("LOCAL_USER", account));
			}
		} else {
			//Running as a server, so check if there are no accounts
			if (accountRepository.count() == 0) {
				//No accounts so create the default one using the settings from the application.properties file
				Account account = new Account(
						setupProperties.getEmail(),
						bCryptPasswordEncoder.encode(setupProperties.getPassword()),
						setupProperties.getName()
				);
				accountRepository.save(account);
				roleRepository.save(new Role("USER", account));
				roleRepository.save(new Role("ADMIN", account));
			}
		}

		//Make the authentication manager use the custom user details service
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	/**
	 * Configures the http security to prevent unauthorised requests to the
	 * account/admin pages as well as enable the login/logout pages
	 *
	 * @param http Spring's http security object that allows configuring web based security for specific http requests
	 *
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//Allow access to the static resources
		http
				.authorizeRequests()
				.antMatchers(
						"/css/**",
						"/js/**",
						"/image/**")
				.permitAll();

		String requiredRole = "USER"; //the required role to access the account settings page

		//Check if running as a client
		if (Arrays.asList(environment.getActiveProfiles()).contains("client")) {
			//Set the required role to "ADMIN" to prevent the local user seeing the account page
			requiredRole = "ADMIN";

			/*
				If running locally, make all pages require authentication to ensure that the
				user is automatically redirected to the login page no matter what page they
				start on
			*/
			http
					.authorizeRequests()
					.antMatchers(
							"/",
							"/terms",
							"/privacy",
							"/help/**")
					.hasAuthority("USER");
		} else {
			//If running as a server, allow access to the home/help pages
			http
					.authorizeRequests()
					.antMatchers(
							"/",
							"/terms",
							"/privacy",
							"/help/**")
					.permitAll();
		}

		//Only users can access the dashboard
		http
				.authorizeRequests()
				.antMatchers("/dashboard/**")
				.hasAuthority("USER");

		//Only "server" based users can modify their account settings
		http
				.authorizeRequests()
				.antMatchers("/account/**")
				.hasAuthority(requiredRole);

		//Only admins can access the admin settings
		http
				.authorizeRequests()
				.antMatchers("/admin/**")
				.hasAuthority("ADMIN");

		//Set the login page
		http
				.formLogin()
                .defaultSuccessUrl("/dashboard/index")
				.loginPage("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.permitAll();

		//Delete the cookies on logout
		http
				.logout()
				.deleteCookies("JSESSIONID");

		//Enable "remember me" support
		http
				.rememberMe()
				.key(securityProperties.getKey());

		//Check if running in development mode
		if (Arrays.asList(environment.getActiveProfiles()).contains("dev")){
			//Fixes access to h2 databsae console
			http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
			http.headers().frameOptions().disable();
		}
	}

	/**
	 * Generates a random password for new accounts or when an admin changes
	 * the password on an account
	 *
	 * @return the random password
	 */
	public static String generateRandomPassword() {
		final Random r = new SecureRandom();
		byte[] b = new byte[12];
		r.nextBytes(b);
		return Base64.encodeBase64String(b);
	}
}
