package uk.ac.warwick.dcs.sherlock.module.web.data.results;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.exception.ResultJobUnsupportedException;
import uk.ac.warwick.dcs.sherlock.engine.report.ReportManager;
import uk.ac.warwick.dcs.sherlock.engine.report.SubmissionSummary;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.SubmissionScore;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.TaskWrapper;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Stores all the data for the job results page
 */
public class JobResultsData {
    /**
     * The job showing the results for
     */
    private IJob job;

    /**
     * The list of tasks from the job
     */
    private List<TaskWrapper> tasks;

    /**
     * A map containing each submission (with the overall score for that
     * submission) and a list of submissions which match with the key. Each
     * submission in that list contains the relative score between the two.
     */
    private Map<SubmissionScore, List<SubmissionScore>> resultsMap;

    /**
     * A map storing the number of submissions in each of the score groups:
     * 0-10, 10-20, 20-30, 30-40, 40-50, 50-60, 60-70, 70-80, 80-90 and 90-100
     */
    private Map<Integer, Integer> groupCounts;

    /**
     * Initialise the data object for the supplied job
     *
     * @param job the job to show the results for
     */
    public JobResultsData(IJob job) {
        this.job = job;
        this.tasks = new ArrayList<>();
        this.job.getTasks().forEach(t -> this.tasks.add(new TaskWrapper(t)));

        this.fillResultsMap();
        this.fillGroupCounts();
    }

    /**
     * Get hte persistent id of the job
     *
     * @return the job id
     */
    public long getPersistentId() {
        return this.job.getPersistentId();
    }

    /**
     * Get the status of the job
     *
     * @return the status as a string
     */
    public String getStatus() {
        return this.job.getStatus().toString();
    }

    /**
     * Get the time the job started
     *
     * @return the time as a formatted string
     */
    public String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        return this.job.getTimestamp().format(formatter);
    }

    /**
     * Get the number of submissions in this job
     *
     * @return the number of submissions
     */
    public int getSubmissionCount() {
        return this.job.getFiles().length;
    }

    /**
     * Get the list of tasks in the job
     *
     * @return the list of tasks, converted to the TaskWrapper object
     */
    public List<TaskWrapper> getTasks() {
        return this.tasks;
    }

    /**
     * Get the job showing the results for
     *
     * @return the job
     */
    public IJob getJob() {
        return job;
    }

    /**
     * Fills the results map using the data from the report generator
     */
    private void fillResultsMap() {
        resultsMap = new HashMap<>();

        ReportManager report = null;
        try {
            report = SherlockEngine.storage.getReportGenerator(job.getLatestResult());
        } catch (ResultJobUnsupportedException e) {
            //No results
            return;
        }

        Map<Long, String> map = new HashMap<>();
        job.getWorkspace().getSubmissions().forEach(s -> map.put(s.getId(), s.getName()));

        for (SubmissionSummary summary : report.getMatchingSubmissions()) {
            String name = "Deleted";
            if (map.containsKey(summary.getPersistentId())) {
                name = map.get(summary.getPersistentId());
            }

            SubmissionScore score = new SubmissionScore(summary.getPersistentId(), name, summary.getScore());

            List<SubmissionScore> list = new ArrayList<>();
            for (ITuple<Long, Float> tuple : summary.getMatchingSubmissions()) {
                String name2 = "Deleted";
                if (map.containsKey(tuple.getKey())) {
                    name2 = map.get(tuple.getKey());
                }

                list.add(new SubmissionScore(tuple.getKey(), name2, tuple.getValue()));
            }

            resultsMap.put(score, list);
        }

//        Generates the fake data
        {
//            for (ISubmission submission : this.job.getWorkspace().getSubmissions()) {
//                SubmissionScore wrapper = new SubmissionScore(submission.getId(), submission.getName(), this.tempRandomScore());
//                List<SubmissionScore> list = new ArrayList<>();
//
//                for (ISubmission match : this.job.getWorkspace().getSubmissions()) {
//                    list.add(new SubmissionScore(match.getId(), match.getName(), this.tempRandomScore()));
//                }
//                resultsMap.put(wrapper, list);
//            }


//            for (int id = 0; id < 5; id++){
//                SubmissionScore wrapper = new SubmissionScore(id, "Submission " + id, this.tempRandomScore());
//                List<SubmissionScore> list = new ArrayList<>();
//
//                int max = this.tempRandomNumberInRange(0, 10);
//                for (int count = 0; count <= max; count++) {
//                    int subId = this.tempRandomNumberInRange(1, 10);
//                    if (subId != id) {
//                        list.add(new SubmissionScore(subId, "Submission " + subId, this.tempRandomScore()));
//                    }
//                }
//                resultsMap.put(wrapper, list);
//            }
        }
    }

    /**
     * Gets the results map
     *
     * @return the map
     */
    public Map<SubmissionScore, List<SubmissionScore>> getResultsMap() {
        return resultsMap;
    }

    /**
     * Fills the group count using the data from the results map
     */
    private void fillGroupCounts() {
        groupCounts = new HashMap<>();

        for (int i = 0; i <= 9; i++) {
            groupCounts.put(i, 0);
        }

        for (Map.Entry<SubmissionScore, List<SubmissionScore>> entry : this.getResultsMap().entrySet()) {
            float score = entry.getKey().getScore();
            int group = entry.getKey().getScoreGroup();
            groupCounts.replace(group, groupCounts.get(group) + 1);
        }
    }

    /**
     * Get the group count map
     *
     * @return the map
     */
    public Map<Integer, Integer> getGroupCounts() {
        return groupCounts;
    }

    /**
     * Convert this object to a JSON object, used by the JavaScript in the UI
     *
     * @return the JSON equivalent of this object
     */
    public String getJSONMap(){
        JSONArray nodes = new JSONArray();
        JSONArray matches = new JSONArray();

        for (Map.Entry<SubmissionScore, List<SubmissionScore>> entry : this.resultsMap.entrySet()) {
            for (SubmissionScore item : entry.getValue()) {
                JSONObject match = new JSONObject();
                match.put("from", entry.getKey().getId());
                match.put("to", item.getId());
                match.put("score", item.getScore());
                match.put("group", item.getScoreGroup());
                matches.put(match);
            }

            JSONObject node = new JSONObject();
            node.put("id", entry.getKey().getId());
            node.put("label", entry.getKey().getName());
            node.put("score", entry.getKey().getScore());
            node.put("group", entry.getKey().getScoreGroup());
            nodes.put(node);
        }

        JSONObject result = new JSONObject();
        result.put("nodes", nodes);
        result.put("matches", matches);
        return result.toString();
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
