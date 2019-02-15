package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

public class ResultsIdWrapper {
    private String name;
    private long id;
    private float score;

    public ResultsIdWrapper(long id, String name, float score) {
        this.name = name;
        this.id = id;
        this.score = score;
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

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getScoreGroup() {
        if (score > 90) {
            return 9;
        } else if (score > 80) {
            return 8;
        } else if (score > 70) {
            return 7;
        } else if (score > 60) {
            return 6;
        } else if (score > 50) {
            return 5;
        } else if (score > 40) {
            return 4;
        } else if (score > 30) {
            return 3;
        } else if (score > 20) {
            return 2;
        } else if (score > 10) {
            return 1;
        }

        return 0;
    }
}
