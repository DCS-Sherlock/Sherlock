package uk.ac.warwick.dcs.sherlock.module.web.data.models.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Code blocks used by the results section of the website
 */
public class CodeBlock {
    /**
     * The start line number of this code block
     */
    private int startLine;

    /**
     * The end line number of this code block
     */
    private int endLine;

    /**
     * If set, this is the ID of the FileMatch this code block links to
     */
    private int matchId;

    /**
     * Initialise this code block without a match
     *
     * @param startLine the start line number
     * @param endLine the end line number
     */
    public CodeBlock(int startLine, int endLine) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.matchId = 0;
    }

    /**
     * Initialise this code block with a match
     *
     * @param startLine the start line number
     * @param endLine the end line number
     * @param matchId the id of the match linked to this block
     */
    public CodeBlock(int startLine, int endLine, int matchId) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.matchId = matchId;
    }

    /**
     * Get the start line number
     *
     * @return the line number
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Get the end line number
     *
     * @return the line number
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Get the match ID linked to this block
     *
     * @return the match ID
     */
    public int getMatchId() {
        return matchId;
    }

    /**
     * Generate a list of line numbers between the start and end
     * line numbers (inclusive)
     *
     * @return the list of line numbers
     */
    public List<Integer> toLineNumList() {
        List<Integer> list = new ArrayList<>();

        for (int i = startLine; i <= endLine; i++) {
            list.add(i);
        }

        return list;
    }

    /**
     * Compare this code block against another object to see if
     * they match
     *
     * @param o the object to compare against
     *
     * @return whether or not the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeBlock codeBlock = (CodeBlock) o;
        return startLine == codeBlock.startLine &&
                endLine == codeBlock.endLine &&
                matchId == codeBlock.matchId;
    }
}
