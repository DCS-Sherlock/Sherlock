package uk.ac.warwick.dcs.sherlock.module.web.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sherlock.webmaster")
public class WebmasterProperties {
    private String institution;
    private String contact;
    private String link;

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
