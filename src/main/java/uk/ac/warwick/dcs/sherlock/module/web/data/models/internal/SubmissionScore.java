package uk.ac.warwick.dcs.sherlock.module.web.data.models.internal;

/**
 * Stores the submission name, id and score to display on the
 * results section of the website
 */
public class SubmissionScore {
    /**
     * The name of the submission
     */
    private String name;

    /**
     * The id of the submission
     */
    private long id;

    /**
     * The score of this submission, which is either:
     * - the overall score if this object is stored in the key of the results map
     * - the relative score if this object is stored in the value of the results map
     */
    private float score;

    /**
     * Initialise the submission score object
     *
     * @param id the id of the submission
     * @param name the name of the submission
     * @param score the score to display to the user
     */
    public SubmissionScore(long id, String name, float score) {
        this.name = name;
        this.id = id;
        this.score = score;
    }

    /**
     * Get the submission name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the submission name
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the submission ID
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the submission ID
     *
     * @param id the new id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the submission score
     *
     * @return the score
     */
    public float getScore() {
        return score;
    }

    /**
     * Set the submission score
     *
     * @param score the new score
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     * All scores are grouped into 10 groups:
     * 0-10, 10-20, 20-30, 30-40, 40-50, 50-60, 60-70, 70-80, 80-90 or 90-100
     * Get the group this score belongs to.
     *
     * @return the score group
     */
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
