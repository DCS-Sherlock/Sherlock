package uk.ac.warwick.dcs.sherlock.module.web.models.results;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.module.web.helpers.ResultsHelper;

import java.util.*;

public class Match {
    private int id;

    private long file1Id;
    private List<CodeBlock> file1CodeBlocks;

    private long file2Id;
    private List<CodeBlock> file2CodeBlocks;

    private String reason;
    private float score;

    private String colour;

    public Match(long file1Id, List<CodeBlock> file1CodeBlocks, long file2Id, List<CodeBlock> file2CodeBlocks, String reason, float score) {
        this.file1Id = file1Id;
        this.file1CodeBlocks = file1CodeBlocks;

        this.file2Id = file2Id;
        this.file2CodeBlocks = file2CodeBlocks;

        this.reason = reason;
        this.score = score;

        this.colour = "#"+ResultsHelper.randomColour();
    }

    public long getFile1Id() {
        return file1Id;
    }

    public List<CodeBlock> getFile1CodeBlocks() {
        return file1CodeBlocks;
    }

    public long getFile2Id() {
        return file2Id;
    }

    public List<CodeBlock> getFile2CodeBlocks() {
        return file2CodeBlocks;
    }

    public String getReason() {
        return reason;
    }

    public float getScore() {
        return score;
    }

    public String getColour() {
        return colour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("file1Id", file1Id);
        result.put("file2Id", file2Id);
        result.put("reason", reason);
        result.put("score", score);
        result.put("colour", colour);

        Set<Integer> list1 = new LinkedHashSet<>();
        file1CodeBlocks.forEach(cb -> list1.addAll(cb.toLineNumList()));
        result.put("file1Lines", new JSONArray(list1));

        Set<Integer> list2 = new LinkedHashSet<>();
        file2CodeBlocks.forEach(cb -> list2.addAll(cb.toLineNumList()));
        result.put("file2Lines", new JSONArray(list2));

        return result;
    }
}
