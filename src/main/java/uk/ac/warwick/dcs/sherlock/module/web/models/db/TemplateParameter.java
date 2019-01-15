package uk.ac.warwick.dcs.sherlock.module.web.models.db;

import javax.persistence.*;

@Entity
@Table(name="template_parameter")
public class TemplateParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private float value;

    @ManyToOne
    @JoinColumn(name = "detector")
    private TemplateDetector detector;

    public TemplateParameter() {

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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
