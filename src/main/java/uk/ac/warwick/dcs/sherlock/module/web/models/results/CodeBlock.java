package uk.ac.warwick.dcs.sherlock.module.web.models.results;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class CodeBlock {
    private int startLine;
    private int endLine;

    private int matchId;

    public CodeBlock(int startLine, int endLine) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.matchId = 0;
    }

    public CodeBlock(int startLine, int endLine, int matchId) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.matchId = matchId;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getMatchId() {
        return matchId;
    }

    public List<Integer> toLineNumList() {
        List<Integer> list = new ArrayList<>();

        for (int i = startLine; i <= endLine; i++) {
            list.add(i);
        }

        return list;
    }

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
