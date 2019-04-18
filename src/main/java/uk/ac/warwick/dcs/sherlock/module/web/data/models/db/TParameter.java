package uk.ac.warwick.dcs.sherlock.module.web.data.models.db;

import javax.persistence.*;

/**
 * The database table storing parameters for a template detector
 */
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
    private double value;

    @Column(name = "postprocessing")
    private boolean postprocessing;

    @ManyToOne
    @JoinColumn(name = "tDetector")
    private TDetector tDetector;

    public TParameter() { }

    public TParameter(String name, float value, boolean postprocessing, TDetector templateDetector) {
        this.name = name;
        this.value = value;
        this.postprocessing = postprocessing;
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
        return (float) value;
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

    public boolean getPostprocessing() {
        return postprocessing;
    }

    public boolean isPostprocessing() {
        return postprocessing;
    }

    public void setPostprocessing(boolean postprocessing) {
        this.postprocessing = postprocessing;
    }
}
