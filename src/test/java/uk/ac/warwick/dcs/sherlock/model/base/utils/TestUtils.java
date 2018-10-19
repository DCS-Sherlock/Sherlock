package model.base.utils;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static File makeFileWithContents(String filename, String contents) throws Exception {
        File file = new File(filename);
        try{
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            writer1.write(contents);
            writer1.close();
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("Could not create a temporary file");
        }
        return file;
    }

    public static File makeFileWithContents(String parentFolder, String filename, String contents) throws Exception {
        File file = new File(parentFolder, filename);
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            writer1.write(contents);
            writer1.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not create a temporary file");
        }
        return file;
    }

    public static List<Token> generateTokensFromUtils(List<TokenUtil> tokenUtilList){
        List<Token> tokenList = new ArrayList<>();
        for (TokenUtil t: tokenUtilList) {
            CommonToken temp = new CommonToken(0);
            temp.setText(t.Text);
            temp.setChannel(t.Channel);
            temp.setLine(t.LineNumber);
            tokenList.add(temp);
        }
        return tokenList;
    }

    public static class TokenUtil {
        public String Text;
        public int Channel;
        public int LineNumber;

        public TokenUtil(String Text, int Channel, int LineNumber){
            this.Text = Text;
            this.Channel = Channel;
            this.LineNumber = LineNumber;
        }
    }
}
