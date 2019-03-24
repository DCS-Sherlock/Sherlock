package uk.ac.warwick.dcs.sherlock.module.web.data.models.db;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="detector")
public class TDetector {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "template")
    private Template template;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tDetector", cascade = CascadeType.REMOVE)
    private Set<TParameter> tParameters = new HashSet<>();

    public TDetector() { }

    public TDetector(String name, Template template) {
        this.name = name;
        this.template = template;
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

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Set<TParameter> getParameters() {
        return tParameters;
    }

    public void setParameters(Set<TParameter> parameters) {
        this.tParameters = parameters;
    }
}
