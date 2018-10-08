/**
 *
 */
package uk.ac.warwick.dcs.sherlock.services.preprocessing;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.List;

/**
 * @author Aliyah
 */
class WhitespacePatternStrategy implements PreProcessingStrategy {

    @Override
    public void preProcessFiles(File[] filePaths, File targetDirectory) {
        for (File file : filePaths) {
            String filename = FilenameUtils.removeExtension(file.getName());
            String finalDestination = targetDirectory + File.separator + filename + ".txt";
            FileInputStream fis = null;

            try {
                /* Open the input file stream */
                fis = new FileInputStream(file);

                /* Create a CharStream that reads in the file */
                CharStream input = CharStreams.fromStream(fis);

                /* Create the Java Lexer and feed it the input */
                WhitespaceLexer lexer = new WhitespaceLexer(input);

                List<? extends Token> list = lexer.getAllTokens();
                Vocabulary vocab = lexer.getVocabulary();

                extractWhitespacePattern(list, vocab, finalDestination);

                /* Close the input stream */
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Extract the whitespace pattern from the input file
     *
     * @throws IOException
     */
    private static void extractWhitespacePattern(List<? extends Token> list, Vocabulary vocab, String outputFile) throws IOException {
        /*Create a buffered writer to store all the tokens in a file */
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)));

        /* For all tokens in the list, add them to the new file created */
        int currentLine = 1;
        for (Token t : list) {
            /* If at least 1 new line has been found, start a new line in the buffered writer */
            if (t.getLine() > currentLine) {
                bw.newLine();                            // Start writing the tokens on the next line

                /* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
                currentLine = t.getLine();                    // Set the currentLine to the line of the token
            }

            if (t.getChannel() == 0) {
                bw.append(vocab.getSymbolicName(t.getType()) + " ");
            }
        }
        /* Close the buffered writer */
        bw.close();
    }

}
