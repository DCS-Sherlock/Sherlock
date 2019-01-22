package uk.ac.warwick.dcs.sherlock.module.web.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sherlock.security")
public class SecurityProperties {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
