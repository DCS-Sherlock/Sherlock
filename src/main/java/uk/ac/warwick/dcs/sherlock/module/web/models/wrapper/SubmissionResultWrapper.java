package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

public class SubmissionResultWrapper {
    private String name;
    private long id;
//    private

    public SubmissionResultWrapper(long id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
