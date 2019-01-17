package uk.ac.warwick.dcs.sherlock.module.web.models.db;

import javax.persistence.*;

@Entity
@Table(name="parameter")
public class TParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private float value;

    @ManyToOne
    @JoinColumn(name = "tDetector")
    private TDetector tDetector;

    public TParameter() { }

    public TParameter(String name, float value, TDetector templateDetector) {
        this.name = name;
        this.value = value;
        this.tDetector = templateDetector;
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

    public TDetector getDetector() {
        return tDetector;
    }

    public void setDetector(TDetector detector) {
        this.tDetector = detector;
    }
}
