package uk.ac.warwick.dcs.sherlock.module.web.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The settings used to setup sherlock on first boot to fetch from the application properties file
 */
@ConfigurationProperties("sherlock.setup")
public class SetupProperties {
    /**
     * The email address of the default admin user
     */
    private String email;

    /**
     * The username of the default admin user
     */
    private String name;

    /**
     * The plaintext password of the default admin user
     */
    private String password;

    /**
     * Get the default user's email address
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the default user's email address attribute in the config file
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the default user's username
     *
     * @return the username
     */
    public String getName() {
        return name;
    }

    /**
     * Set the default user's username attribute in the config file
     *
     * @param name the new username
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the default user's plaintext password
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the default user's plaintext password attribute in the config file
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
