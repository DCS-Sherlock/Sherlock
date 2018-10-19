package model.base.utils;

import org.antlr.v4.runtime.Token;

import java.util.*;
import java.util.Arrays;

public class TestFirstJavaFile implements TestJavaFile {
    private final String Original = "//comment\npublic" + " " + " " + "class {}";
    private List<TestUtils.TokenUtil> list = new ArrayList<>(
            Arrays.asList(
                new TestUtils.TokenUtil("//comment",4,1),
                    new TestUtils.TokenUtil("public",0,2),
                    new TestUtils.TokenUtil("  ",3,2),
                    new TestUtils.TokenUtil("class",0,2),
                    new TestUtils.TokenUtil(" ",2,2),
                    new TestUtils.TokenUtil("{",0,2),
                    new TestUtils.TokenUtil("}",0,2)
            )
        );
    @Override
    public String Original(){
        return Original;
    }

    @Override
    public List<Token> getTokens() {
       return TestUtils.generateTokensFromUtils(list);
    }

    @Override
    public List<String> TrimWhiteSpaceOnly(){
        return new ArrayList<>( Arrays.asList(
                "//comment", "public", " ", "class", " ", "{", "}")
        );
    }

    @Override
    public List<String> CommentRemover() {
        return new ArrayList<>(Arrays.asList(
                "public", "  ", "class", " ", "{", "}")
        );
    }

    @Override
    public List<String> CommentExtractor() {
        return new ArrayList<>(Arrays.asList(
                "//comment")
        );
    }

    @Override
    public List<String> StandardStringifier() {
        return new ArrayList<>(Arrays.asList(
                "//comment", "public  class {}")
        );
    }

    @Override
    public List<String> StandardTokeniser() {
        return new ArrayList<>(Arrays.asList(
                "LINE_COMMENT",
                "PUBLIC CLASS LBRACE RBRACE")
        );
    }
}
