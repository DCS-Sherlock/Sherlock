package uk.ac.warwick.dcs.sherlock.module.web.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The security settings to fetch from the application properties file
 */
@ConfigurationProperties("sherlock.security")
public class SecurityProperties {
    /**
     * The secret security key used when generating the contents of the remember me token
     */
    private String key;

    /**
     * Gets the secret key
     *
     * @return the secret key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the secret key attribute in the config file
     *
     * @param key the new security key
     */
    public void setKey(String key) {
        this.key = key;
    }
}
