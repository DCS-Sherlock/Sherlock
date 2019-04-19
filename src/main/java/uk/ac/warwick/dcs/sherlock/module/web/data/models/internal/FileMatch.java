package uk.ac.warwick.dcs.sherlock.module.web.data.models.internal;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.report.SubmissionMatch;
import uk.ac.warwick.dcs.sherlock.engine.report.SubmissionMatchItem;
import uk.ac.warwick.dcs.sherlock.module.web.data.results.ResultsHelper;

import java.util.*;

/**
 * Stores the details of a match between two files
 */
public class FileMatch {
    /**
     * The ID of this file match
     */
    private int id;

    /**
     * The map of files involved in the match to the matched blocks
     */
    private Map<ISourceFile, List<CodeBlock>> map;

    /**
     * The reason this match was detected
     */
    private String reason;

    /**
     * The score associated with this match
     */
    private float score;

    /**
     * The colour to highlight the lines of this match
     */
    private String colour;

    /**
     * Initialise this match
     *
     * @param match the engine match to initialise this object with
     */
    public FileMatch(SubmissionMatch match) {
        this.map = new HashMap<>();
        this.reason = match.getReason();

        for (SubmissionMatchItem item : match.getItems()) {
            List<CodeBlock> blocks = new ArrayList<>();
            item.GetLineNumbers().forEach(t -> blocks.add(new CodeBlock(t.getKey(), t.getValue())));
            map.put(item.GetFile(), blocks);
            this.score = item.GetScore() * 100;
        }
//        this.score = match.getScore();

        //Generate a random colour
        this.colour = ResultsHelper.randomColour();
    }

    /**
     * Get the map
     *
     * @return the map
     */
    public Map<ISourceFile, List<CodeBlock>> getMap() {
        return map;
    }

    /**
     * Get the list of code blocks for a file
     *
     * @param file the file to get the blocks for
     *
     * @return the list
     */
    public List<CodeBlock> getCodeBlocks(ISourceFile file) {
        return map.getOrDefault(file, new ArrayList<>());
    }
    /**
     * Get the reason for this match
     *
     * @return the reason text
     */
    public String getReason() {
        return reason;
    }

    /**
     * Get the score for this match
     *
     * @return the score
     */
    public float getScore() {
        return score;
    }

    /**
     * Get the highlight CSS colour
     *
     * @return the colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * Get the id of this match
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id for this match, also updates the colour
     *
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
        this.colour = ResultsHelper.getColour(id);
    }

    /**
     * Get a string listing all the line numbers involved from the file
     *
     * @param file the file to get the list for
     *
     * @return the comma separated list
     */
    public String getFileLines(ISourceFile file) {
        return getLines(getCodeBlocks(file));
    }

    /**
     * Given a list of code blocks, generates a comma separated string
     * of the line numbers involved in this match
     *
     * e.g. 2,5-10,19-20
     *
     * @param list the list of code blocks to use
     *
     * @return the comma separated list
     */
    private String getLines(List<CodeBlock> list) {
        List<String> lines = new ArrayList<>();

        for (CodeBlock cb : list) {
            if (cb.getStartLine() == cb.getEndLine()) {
                lines.add(cb.getStartLine()+"");
            } else {
                lines.add(cb.getStartLine() + "-" + cb.getEndLine());
            }
        }

        return String.join(", ", lines);
    }

    /**
     * Convert this object to a JSON object, used by the JavaScript in the UI
     *
     * @return the JSON equivalent of this object
     */
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("reason", reason);
        result.put("score", score);
        result.put("colour", colour);

        JSONArray matches = new JSONArray();
        for (Map.Entry<ISourceFile, List<CodeBlock>> entry : map.entrySet()) {
            ISourceFile entryFile = entry.getKey();
            List<CodeBlock> entryList = entry.getValue();

            JSONObject match = new JSONObject();

            match.put("id", entryFile.getPersistentId());
            match.put("name", entryFile.getFileIdentifier());
            match.put("displayName", entryFile.getFileDisplayName());
            match.put("submission", entryFile.getSubmission().getId());
            match.put("submissionName", entryFile.getSubmission().getName());

            Set<Integer> lines = new LinkedHashSet<>();
            entryList.forEach(cb -> lines.addAll(cb.toLineNumList()));
            match.put("lines", new JSONArray(lines));

            matches.put(match);
        }

        result.put("matches", matches);

        return result;
    }
}
