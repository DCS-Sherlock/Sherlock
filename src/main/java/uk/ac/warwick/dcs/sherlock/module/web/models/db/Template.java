package uk.ac.warwick.dcs.sherlock.module.web.models.db;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="template")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "language")
    private Language language;

    @Column(name = "is_public")
    private boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "template", cascade = CascadeType.REMOVE)
    private Set<TDetector> tDetectors = new HashSet<>();

    public Template() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Set<TDetector> getDetectors() {
        return tDetectors;
    }

    public void setDetectors(Set<TDetector> detectors) {
        this.tDetectors = detectors;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
