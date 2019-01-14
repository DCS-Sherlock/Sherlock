package uk.ac.warwick.dcs.sherlock.module.web.models.db;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="template_detector")
public class TemplateDetector {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "template")
    private Template template;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "detector", cascade = CascadeType.REMOVE)
    private Set<TemplateParameter> parameters = new HashSet<>();

    public TemplateDetector() { }

    public TemplateDetector(String name, Template template) {
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

    public Set<TemplateParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<TemplateParameter> parameters) {
        this.parameters = parameters;
    }
}
