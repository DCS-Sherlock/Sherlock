package uk.ac.warwick.dcs.sherlock.module.web.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The details of the people running the current instance of Sherlock to fetch from the application properties file
 */
@ConfigurationProperties("sherlock.webmaster")
public class WebmasterProperties {
    /**
     * The name of the organisation/company/institution
     */
    private String institution;

    /**
     * The name of the individual for users to contact
     */
    private String contact;

    /**
     * The link to contact the named individual/instituion
     */
    private String link;

    /**
     * Get the name of the institution
     *
     * @return the name of the institution
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * Set the institution name attribute in the config file
     *
     * @param institution the new name
     */
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    /**
     * Get the contact name
     *
     * @return the contact name
     */
    public String getContact() {
        return contact;
    }

    /**
     * Set the contact name attribute in the config file
     *
     * @param contact the new name
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Get the link to contact the institution/person
     *
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Set the contact link attribute in the config file
     *
     * @param link the new link
     */
    public void setLink(String link) {
        this.link = link;
    }
}
