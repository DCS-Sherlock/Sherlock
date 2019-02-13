package uk.ac.warwick.dcs.sherlock.module.web.configurations;

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
import uk.ac.warwick.dcs.sherlock.module.web.properties.SecurityProperties;
import uk.ac.warwick.dcs.sherlock.module.web.properties.SetupProperties;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.RoleRepository;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	};

	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (Arrays.asList(environment.getActiveProfiles()).contains("client")) {
			//Try to find the "local user"
			String email = "local.sherlock@example.com";
			Account account = accountRepository.findByEmail(email);

			//Add the "local user" if not found
			if (account == null) {
				account = new Account(email, bCryptPasswordEncoder.encode("local_password"), "Local User");
				accountRepository.save(account);
				roleRepository.save(new Role("USER", account));
				roleRepository.save(new Role("LOCAL_USER", account));
			}
		} else {
			if (accountRepository.count() == 0) {
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

		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers(
						"/css/**",
						"/js/**",
						"/image/**")
				.permitAll();

		String accountRole = "USER";
		if (Arrays.asList(environment.getActiveProfiles()).contains("client")) {
			accountRole = "ADMIN";
			http
					.authorizeRequests()
					.antMatchers(
							"/",
							"/terms",
							"/privacy",
							"/help/**")
					.hasAuthority("USER");
		} else {
			http
					.authorizeRequests()
					.antMatchers(
							"/",
							"/terms",
							"/privacy",
							"/help/**")
					.permitAll();
		}

		http
				.authorizeRequests()
				.antMatchers("/dashboard/**")
				.hasAuthority("USER");

		http
				.authorizeRequests()
				.antMatchers("/account/**")
				.hasAuthority(accountRole);

		http
				.authorizeRequests()
				.antMatchers("/admin/**")
				.hasAuthority("ADMIN");

		http
				.formLogin()
                .defaultSuccessUrl("/dashboard/index")
				.loginPage("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.permitAll();

		http
				.logout()
				.deleteCookies("JSESSIONID");

		http
				.rememberMe()
				.key(securityProperties.getKey());

		//Fixes access to h2 console in dev mode
		if (Arrays.asList(environment.getActiveProfiles()).contains("dev")){
			http
					.authorizeRequests()
					.antMatchers("/h2-console/**")
					.permitAll();

			http.headers().frameOptions().disable();
		}
	}

	public static String generateRandomPassword() {
		final Random r = new SecureRandom();
		byte[] b = new byte[12];
		r.nextBytes(b);
		return Base64.encodeBase64String(b);
	}
}
