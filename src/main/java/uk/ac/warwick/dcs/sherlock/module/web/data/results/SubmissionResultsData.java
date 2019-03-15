package uk.ac.warwick.dcs.sherlock.module.web.data.results;

import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.engine.report.ReportManager;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.CodeBlock;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.FileMatch;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.SubmissionScore;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.MapperException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stores all the data for the compare submissions page or the submission report page
 */
public class SubmissionResultsData {
    /**
     * The first submission
     */
    private ISubmission submission1;

    /**
     * The second submission, null if not comparing
     */
    private ISubmission submission2;

    /**
     * The list of matches between the submissions
     */
    private List<FileMatch> matches;

    /**
     * The list of submissions matched to this one
     */
    private List<SubmissionScore> submissions;

    /**
     * The file mapper linking the lines in each file to a match
     */
    private FileMapper fileMapper;

    /**
     * The score for the first submission
     */
    private float score;

    /**
     * Initialise the report data object
     *
     * @param submission the submission to report
     *
     * @throws MapperException thrown if the FileMapper isn't initialised correctly
     */
    public SubmissionResultsData(ISubmission submission) throws MapperException {
        this.submission1 = submission;

        this.matches = new ArrayList<>();
        this.submissions = new ArrayList<>();

        ReportManager report = new ReportManager(); //TODO: load with data



        //Generates fake data
        {
            long file1Id = submission.getAllFiles().get(0).getPersistentId();
//            long file2Id = submission.getAllFiles().get(0).getPersistentId();
            long file2Id = 2;
            String file1Name = submission1.getAllFiles().get(0).getFileDisplayName();
            String file2Name = "SherlockHelper.java";

            for (int i = 1; i < 50; i++) {
                List<CodeBlock> list1 = new ArrayList<>();
                list1.add(new CodeBlock(this.tempRandomNumberInRange((i*2)*2, (i*2)+3), this.tempRandomNumberInRange((i*2)+3, (i*2)+6)));
                List<CodeBlock> list2 = new ArrayList<>();
                list2.add(new CodeBlock(this.tempRandomNumberInRange((i*2), (i*2)+3), this.tempRandomNumberInRange((i*2)+3, (i*2)+6)));

                matches.add(new FileMatch(file1Id, file1Name, submission.getId(), list1, file2Id, file2Name, 2, list2, "Match "+ i +" Reason", this.tempRandomNumberInRange(0, 100)));
                if (i == 1) {
                    matches.add(new FileMatch(file1Id, file1Name, submission.getId(), list1, file2Id, file2Name, 2, list2, "Match "+ i +" COPY Reason", this.tempRandomNumberInRange(0, 100)));
                }
            }

            int max = this.tempRandomNumberInRange(0, 10);
            for (int count = 0; count <= max; count++) {
                int subId = this.tempRandomNumberInRange(1, 10);
                if (subId != submission.getId()) {
                    submissions.add(new SubmissionScore(subId, "Submission " + subId, this.tempRandomScore()));
                }
            }
            this.score = tempRandomScore();
        }

        //Loop through the matches, setting the ids
        for (int i = 0; i < matches.size(); i++) {
            FileMatch match = matches.get(i);
            match.setId(i);
        }

        //Initialise the file mapper using the list of matches
        this.fileMapper = new FileMapper(this.matches);
    }

    /**
     * Initialise the comparison data object
     *
     * @param submission1 the first submission to compare
     * @param submission2 the second submission to compare
     *
     * @throws MapperException thrown if the FileMapper isn't initialised correctly
     */
    public SubmissionResultsData(ISubmission submission1, ISubmission submission2) throws MapperException {
        this.submission1 = submission1;
        this.submission2 = submission2;

        this.matches = new ArrayList<>();
        this.submissions = new ArrayList<>();
        this.score = 0;

        //TODO: fetch real data from report generator

        //Generates fake data
        {
            long file1Id = submission1.getAllFiles().get(0).getPersistentId();
            long file2Id = submission2.getAllFiles().get(0).getPersistentId();
            String file1Name = submission1.getAllFiles().get(0).getFileDisplayName();
            String file2Name = submission2.getAllFiles().get(0).getFileDisplayName();

            for (int i = 1; i < 50; i++) {
                List<CodeBlock> list1 = new ArrayList<>();
                list1.add(new CodeBlock(this.tempRandomNumberInRange((i*2)*2, (i*2)+3), this.tempRandomNumberInRange((i*2)+3, (i*2)+6)));
                List<CodeBlock> list2 = new ArrayList<>();
                list2.add(new CodeBlock(this.tempRandomNumberInRange((i*2), (i*2)+3), this.tempRandomNumberInRange((i*2)+3, (i*2)+6)));

                matches.add(new FileMatch(file1Id, file1Name, submission1.getId(), list1, file2Id, file2Name, submission2.getId(), list2, "Match "+ i +" Reason", this.tempRandomNumberInRange(0, 100)));
                if (i == 1) {
                    matches.add(new FileMatch(file1Id, file1Name, submission1.getId(), list1, file2Id, file2Name, submission2.getId(), list2, "Match "+ i +" COPY Reason", this.tempRandomNumberInRange(0, 100)));
                }
            }
        }

        //Loop through the matches, setting the ids
        for (int i = 0; i < matches.size(); i++) {
            FileMatch match = matches.get(i);
            match.setId(i);
        }

        //Initialise the file mapper using the list of matches
        this.fileMapper = new FileMapper(this.matches);
    }

    /**
     * Get the first submission
     *
     * @return the submission
     */
    public ISubmission getSubmission1() {
        return submission1;
    }

    /**
     * Get the list of matches
     *
     * @return the list
     */
    public List<FileMatch> getMatches() {
        return matches;
    }

    /**
     * Gets the list of submissions linked to this one
     *
     * @return the list
     */
    public List<SubmissionScore> getSubmissions() {
        return submissions;
    }

    /**
     * Get the second submission
     *
     * @return the submission
     */
    public ISubmission getSubmission2() {
        return submission2;
    }

    /**
     * Get the first submission's score
     *
     * @return the score
     */
    public float getScore() {
        return score;
    }

    /**
     * All scores are grouped into 10 groups:
     * 0-10, 10-20, 20-30, 30-40, 40-50, 50-60, 60-70, 70-80, 80-90 or 90-100
     * Get the group this score belongs to.
     *
     * @return the score group
     */
    public int getScoreGroup() {
        return ResultsHelper.getScoreGroup(this.score);
    }

    /**
     * Get the file mapper as a JSON string
     *
     * @return the JSON object as a string
     */
    public String getMapJSON(){
        return fileMapper.toJSON().toString();
    }

    /**
     * Get the list of matches as a JSON string
     *
     * @return the JSON list as a string
     */
    public String getMatchesJSON() {
        JSONObject object = new JSONObject();

        for (FileMatch match : matches) {
            object.put(""+match.getId(), match.toJSON());
        }

        return object.toString();
    }

    /**
     * Get the list of plagiarised line numbers converted to a comma separated list for
     * a specific file in the map
     *
     * e.g. if lines 2-10 are mapped to a match, this would return "2,3,4,5,6,7,8,9,10"
     *
     * @param fileId the id of the file to get the highlighted lines for
     *
     * @return the comma separated list, or an empty list if the file isn't found
     */
    public String getHighlightedLines(long fileId) {
        return fileMapper.getHighlightedLines(fileId);
    }


    //TODO: Remove when loading real data
    private float tempRandomScore() {
        return this.tempRandomNumberInRange(1, 100);
    }

    //TODO: Remove when loading real data
    @SuppressWarnings("Duplicates")
    private int tempRandomNumberInRange(int min, int max) {
        if (min >= max) {
            return max;
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
