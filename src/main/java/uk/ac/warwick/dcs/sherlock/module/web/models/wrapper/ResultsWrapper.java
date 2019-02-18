package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.engine.component.IJob;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class ResultsWrapper {
    private IJob job;
    private List<TaskWrapper> tasks;

    private Map<ResultsIdWrapper, List<ResultsIdWrapper>> resultsMap;
    private Map<Integer, Integer> groupCounts;

    public ResultsWrapper(IJob job) {
        this.job = job;
        this.tasks = new ArrayList<>();
        this.job.getTasks().forEach(t -> this.tasks.add(new TaskWrapper(t)));

        this.resultsMap = this.generateResultsMap();
        this.groupCounts = this.generateGroupCounts();
    }

    public long getPersistentId() {
        return this.job.getPersistentId();
    }

    public String getStatus() {
        return this.job.getStatus().toString();
    }

    public String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        return this.job.getTimestamp().format(formatter);
    }

    public int getSubmissionCount() {
        return this.job.getFiles().length;
    }

    public List<TaskWrapper> getTasks() {
        return this.tasks;
    }

    //TODO: Actually load the real results, not random data
    private Map<ResultsIdWrapper, List<ResultsIdWrapper>> generateResultsMap() {
        Map<ResultsIdWrapper, List<ResultsIdWrapper>> map = new HashMap<>();

        for (int id = 0; id < 50; id++){
            ResultsIdWrapper wrapper = new ResultsIdWrapper(id, "Submission " + id, this.tempRandomScore());
            List<ResultsIdWrapper> list = new ArrayList<>();

            int max = this.tempRandomNumberInRange(0, 10);
            for (int count = 0; count <= max; count++) {
                int subId = this.tempRandomNumberInRange(1, 10);
                if (subId != id) {
                    list.add(new ResultsIdWrapper(subId, "Submission " + subId, this.tempRandomScore()));
                }
            }
            map.put(wrapper, list);
        }

        return map;
    }

    public Map<ResultsIdWrapper, List<ResultsIdWrapper>> getResultsMap() {
        return resultsMap;
    }

    private Map<Integer, Integer> generateGroupCounts() {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i <= 9; i++) {
            map.put(i, 0);
        }

        for (Map.Entry<ResultsIdWrapper, List<ResultsIdWrapper>> entry : this.getResultsMap().entrySet()) {
            float score = entry.getKey().getScore();
            int group = entry.getKey().getScoreGroup();
            map.replace(group, map.get(group) + 1);
        }

        return map;
    }

    public Map<Integer, Integer> getGroupCounts() {
        return groupCounts;
    }

    public String getJSONMap(){
        JSONArray nodes = new JSONArray();
        JSONArray matches = new JSONArray();

        for (Map.Entry<ResultsIdWrapper, List<ResultsIdWrapper>> entry : this.resultsMap.entrySet()) {
            for (ResultsIdWrapper item : entry.getValue()) {
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

    private float tempRandomScore() {
        return this.tempRandomNumberInRange(1, 100);
    }

    private int tempRandomNumberInRange(int min, int max) {
        if (min >= max) {
            return max;
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
