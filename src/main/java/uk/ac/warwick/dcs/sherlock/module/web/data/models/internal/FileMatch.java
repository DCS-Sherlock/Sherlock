package uk.ac.warwick.dcs.sherlock.module.web.data.models.internal;

import org.json.JSONArray;
import org.json.JSONObject;
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
     * The ID of the first file
     */
    private long file1Id;

    /**
     * The name of the first file
     */
    private String file1Name;

    /**
     * The submission id of the first file
     */
    private long file1Submission;

    /**
     * The list of code blocks in the first file, plagiarised from the second
     */
    private List<CodeBlock> file1CodeBlocks;

    /**
     * The ID of the second file
     */
    private long file2Id;

    /**
     * The name of the second file
     */
    private String file2Name;

    /**
     * The submission id of the second file
     */
    private long file2Submission;

    /**
     * The list of code blocks in the second file, plagiarised from the first
     */
    private List<CodeBlock> file2CodeBlocks;

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
     * @param file1Id the first file id
     * @param file1Name the first file name
     * @param file1CodeBlocks the matches from the first file
     * @param file2Id the second file id
     * @param file2Name the second file name
     * @param file2CodeBlocks the matches from the second file
     * @param reason the reason text
     * @param score the score associated with this match
     */
    public FileMatch(long file1Id, String file1Name, long file1Submission, List<CodeBlock> file1CodeBlocks, long file2Id, String file2Name, long file2Submission, List<CodeBlock> file2CodeBlocks, String reason, float score) {
        this.file1Id = file1Id;
        this.file1Name = file1Name;
        this.file1Submission = file1Submission;
        this.file1CodeBlocks = file1CodeBlocks;

        this.file2Id = file2Id;
        this.file2Name = file2Name;
        this.file2Submission = file2Submission;
        this.file2CodeBlocks = file2CodeBlocks;

        this.reason = reason;
        this.score = score;

        //Generate a random colour
        this.colour = ResultsHelper.randomColour();
    }

    /**
     * Get the id of the first file
     *
     * @return the file id
     */
    public long getFile1Id() {
        return file1Id;
    }

    /**
     * Get the name of the first file
     *
     * @return the file name
     */
    public String getFile1Name() {
        return file1Name;
    }

    /**
     * Get the list of code blocks from the first file
     *
     * @return the list of code blocks
     */
    public List<CodeBlock> getFile1CodeBlocks() {
        return file1CodeBlocks;
    }

    /**
     * Get the id of the second file
     *
     * @return the file id
     */
    public long getFile2Id() {
        return file2Id;
    }

    /**
     * Get the name of the second file
     *
     * @return the file name
     */
    public String getFile2Name() {
        return file2Name;
    }

    /**
     * Get the list of code blocks from the second file
     *
     * @return the list of code blocks
     */
    public List<CodeBlock> getFile2CodeBlocks() {
        return file2CodeBlocks;
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
     * Get a string listing all the line numbers involved from the first file
     *
     * @return the comma separated list
     */
    public String getFile1Lines() {
        return getLines(file1CodeBlocks);
    }

    /**
     * Get a string listing all the line numbers involved from the second file
     *
     * @return the comma separated list
     */
    public String getFile2Lines() {
        return getLines(file2CodeBlocks);
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

        for (CodeBlock cb : file1CodeBlocks) {
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

        result.put("file1Id", file1Id);
        result.put("file2Id", file2Id);
        result.put("file1Name", file1Name);
        result.put("file2Name", file2Name);
        result.put("file1Submission", file1Submission);
        result.put("file2Submission", file2Submission);
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
