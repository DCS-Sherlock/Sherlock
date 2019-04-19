package uk.ac.warwick.dcs.sherlock.module.web.data.results;

import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;
import uk.ac.warwick.dcs.sherlock.engine.exception.ResultJobUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.report.ReportManager;
import uk.ac.warwick.dcs.sherlock.engine.report.SubmissionMatch;
import uk.ac.warwick.dcs.sherlock.engine.report.SubmissionMatchGroup;
import uk.ac.warwick.dcs.sherlock.engine.report.SubmissionSummary;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.CodeBlock;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.FileMatch;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.SubmissionScore;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.MapperException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores all the data for the compare submissions page or the submission report page
 */
public class SubmissionResultsData {
    /**
     * The job showing the results for
     */
    private IJob job;

    /**
     * The summary (if it's a report)
     */
    private String summary = "";

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
     * @param job the job getting the results from
     * @param submission the submission to report
     *
     * @throws MapperException thrown if the FileMapper isn't initialised correctly
     */
    public SubmissionResultsData(IJob job, ISubmission submission) throws MapperException {
        this.job = job;
        this.submission1 = submission;

        this.matches = new ArrayList<>();
        this.submissions = new ArrayList<>();

        ReportManager report = null;
        try {
            report = SherlockEngine.storage.getReportGenerator(job.getLatestResult());
        } catch (ResultJobUnsupportedException e) {
            // No results
            // e.printStackTrace();
        }

        if (report != null) {
            //Get the list of submission match groups and the report summary
            ITuple<List<SubmissionMatchGroup>, String> result = report.GetSubmissionReport(submission1);

            //Set the summary
            this.summary = result.getValue();

            //Loop through the submission groups, adding all the matches
            result.getKey().forEach(group -> group.GetMatches().forEach(m -> this.matches.add(new FileMatch(m))));

            //Fetch the submission summary for this submission
            List<SubmissionSummary> summaryList = report.GetMatchingSubmissions()
                    .stream().filter(s -> s.getPersistentId() == submission1.getId())
                    .collect(Collectors.toList());

            //Create a map linking submission ids to their names
            Map<Long, String> idToName = new HashMap<>();
            job.getWorkspace().getSubmissions().forEach(s -> idToName.put(s.getId(), s.getName()));

            if (summaryList.size() == 1) {
                SubmissionSummary summary = summaryList.get(0);

                for (ITuple<Long, Float> tuple : summary.getMatchingSubmissions()) {
                    String matchName = idToName.getOrDefault(tuple.getKey(), "Deleted");

                    submissions.add(new SubmissionScore(tuple.getKey(), matchName, tuple.getValue() * 100));
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
     * Initialise the comparison data object
     *
     * @param job the job getting the results from
     * @param submission1 the first submission to compare
     * @param submission2 the second submission to compare
     *
     * @throws MapperException thrown if the FileMapper isn't initialised correctly
     */
    public SubmissionResultsData(IJob job, ISubmission submission1, ISubmission submission2) throws MapperException {
        this.job = job;
        this.submission1 = submission1;
        this.submission2 = submission2;

        this.matches = new ArrayList<>();
        this.submissions = new ArrayList<>();
        this.score = 0;

        ReportManager report = null;
        try {
            report = SherlockEngine.storage.getReportGenerator(job.getLatestResult());
        } catch (ResultJobUnsupportedException e) {
            // No results
            // e.printStackTrace();
        }

        if (report != null) {
            List<ISubmission> compare = new ArrayList<>();
            compare.add(submission1);
            compare.add(submission2);

            //Loop through the submission groups, adding all the matches
            List<SubmissionMatchGroup> list = report.GetSubmissionComparison(compare);
            list.forEach(group -> group.GetMatches().forEach(m -> this.matches.add(new FileMatch(m))));
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
     * Get the summary for the report, replacing line breaks with html line breaks
     *
     * @return the summary
     */
    public String getSummary() {
        return this.summary.replaceAll("(\r\n|\n)", "<br />");
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
     * Get the second submission
     *
     * @return the submission
     */
    public ISubmission getSubmission2() {
        return submission2;
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
     *
     * @return
     */
    public Map<ISubmission, SortedMap<Long, ISourceFile>> getMatchedFiles() {
        Map<ISubmission, SortedMap<Long, ISourceFile>> map = new HashMap<>();

        for (FileMatch match : matches) {
            for (Map.Entry<ISourceFile, List<CodeBlock>> entry : match.getMap().entrySet()) {
                ISourceFile entryFile = entry.getKey();

                if (!entryFile.getSubmission().equals(this.submission1)) {
                    if (!map.containsKey(entryFile.getSubmission())) {
                        map.put(entryFile.getSubmission(), new TreeMap<>());
                    }

                    if (!map.get(entryFile.getSubmission()).containsKey(entryFile.getPersistentId())) {
                        map.get(entryFile.getSubmission()).put(entryFile.getPersistentId(), entryFile);
                    }
                }
            }
        }

        return map;
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
}
