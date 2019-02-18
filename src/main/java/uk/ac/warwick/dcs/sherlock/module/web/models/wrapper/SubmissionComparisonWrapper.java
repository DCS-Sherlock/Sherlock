package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

public class SubmissionComparisonWrapper {
    private SubmissionResultWrapper submission1;
    private SubmissionResultWrapper submission2;

    public SubmissionComparisonWrapper(SubmissionResultWrapper submission1, SubmissionResultWrapper submission2) {
        this.submission1 = submission1;
        this.submission2 = submission2;
    }
}
