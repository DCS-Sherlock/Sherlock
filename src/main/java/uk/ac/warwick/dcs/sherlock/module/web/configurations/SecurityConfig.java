package uk.ac.warwick.dcs.sherlock.module.web.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment environment;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        if (Arrays.asList(environment.getActiveProfiles()).contains("client")) {
            auth.inMemoryAuthentication().withUser("local_user").password("{noop}local_password").roles("USER", "LOCAL_USER");
        } else {
            auth.inMemoryAuthentication().withUser("server_user").password("{noop}server_password").roles("USER");
        }
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/css/**", "/js/**", "/image/**", "/", "/info/**", "/login", "/register").permitAll().antMatchers("/dashboard/**").hasRole("USER").anyRequest()
				.authenticated().and().formLogin().loginPage("/login").failureUrl("/login?error").usernameParameter("username").passwordParameter("password").permitAll().and().logout()
				.logoutSuccessUrl("/login?logout").permitAll().and().csrf().disable() //TODO: Re-enable later
		;
	}
}
