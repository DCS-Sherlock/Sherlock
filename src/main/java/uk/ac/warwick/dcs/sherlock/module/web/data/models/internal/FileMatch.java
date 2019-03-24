package uk.ac.warwick.dcs.sherlock.module.web.data.models.internal;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;
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
     * The first file
     */
    private ISourceFile file1;

    /**
     * The second file
     */
    private ISourceFile file2;

    /**
     * The submission of the first file
     */
    private ISubmission submission1;

    /**
     * The submission of the second file
     */
    private ISubmission submission2;

    /**
     * The list of code blocks in the first file, plagiarised from the second
     */
    private List<CodeBlock> file1CodeBlocks;

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
     * @param file1 the first file
     * @param submission1 the submission of the first file
     * @param file1CodeBlocks the matches from the first file
     * @param file2 the first file
     * @param submission2 the submission of the first file
     * @param file2CodeBlocks the matches from the second file
     * @param reason the reason text
     * @param score the score associated with this match
     */
    public FileMatch(
            ISourceFile file1,
            ISubmission submission1,
            List<CodeBlock> file1CodeBlocks,
            ISourceFile file2,
            ISubmission submission2,
            List<CodeBlock> file2CodeBlocks,
            String reason,
            float score) {
        this.file1 = file1;
        this.submission1 = submission1;
        this.file1CodeBlocks = file1CodeBlocks;

        this.file2 = file2;
        this.submission2 = submission2;
        this.file2CodeBlocks = file2CodeBlocks;

        this.reason = reason;
        this.score = score;

        //Generate a random colour
        this.colour = ResultsHelper.randomColour();
    }

    /**
     * Get the first file
     *
     * @return the file
     */
    public ISourceFile getFile1() {
        return file1;
    }

    /**
     * Get the second file
     *
     * @return the file
     */
    public ISourceFile getFile2() {
        return file2;
    }

    /**
     * Get the submission of the first file
     *
     * @return the submission
     */
    public ISubmission getSubmission1() {
        return submission1;
    }

    /**
     * Get the submission of the second file
     *
     * @return the submission
     */
    public ISubmission getSubmission2() {
        return submission2;
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

        result.put("file1Id", file1.getPersistentId());
        result.put("file2Id", file2.getPersistentId());
        result.put("file1Name", file1.getFileIdentifier());
        result.put("file2Name", file2.getFileIdentifier());
        result.put("file1DisplayName", file1.getFileDisplayName());
        result.put("file2DisplayName", file2.getFileDisplayName());
        result.put("file1Submission", submission1.getId());
        result.put("file2Submission", submission2.getId());
        result.put("file1SubmissionName", submission1.getName());
        result.put("file2SubmissionName", submission2.getName());
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
