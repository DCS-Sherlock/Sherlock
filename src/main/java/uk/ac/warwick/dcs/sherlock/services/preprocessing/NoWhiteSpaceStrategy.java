package uk.ac.warwick.dcs.sherlock.services.preprocessing;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.List;

public class NoWhiteSpaceStrategy implements PreProcessingStrategy
{

    @Override
    public void preProcessFiles(File[] filePaths, File targetDirectory)
    {
        System.out.println("Removing White Space");

        for (File file : filePaths)
        {
            System.out.println("File " + file.getAbsolutePath());

            String filename = FilenameUtils.removeExtension(file.getName());
            String finalDestination = targetDirectory + File.separator + filename + ".txt";
            System.out.println("finalDestination " + finalDestination);
            FileInputStream fis = null;

            try
            {
                /* Open the input file stream */
                fis = new FileInputStream(file);

                /* Create a CharStream that reads in the file */
                CharStream input = CharStreams.fromStream(fis);

                /* Create the Java Lexer and feed it the input */
                NowhitespaceLexer lexer = new NowhitespaceLexer(input);

                List<? extends Token> list = lexer.getAllTokens();
                Vocabulary vocab = lexer.getVocabulary();

                removeWhitespace(list, finalDestination);

                /* Close the input stream */
                fis.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove any unnecessary whitespace (e.g. \t and double spaces)
     *
     * @throws IOException
     */
    private static void removeWhitespace(List<? extends Token> list, String outputFile) throws IOException
    {
        System.out.println("Remove WS ");
        System.out.println("Target File: " + outputFile);
        //		String destination = System.getProperty("user.home") + File.separator + "Result" + File.separator + "NoWhiteSpace.txt" ;

        /*Create a buffered writer to store all the tokens in a file */
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)));

        /* For all tokens in the list, add them to the new file created */
        int currentLine = 1;
        for (Token t : list)
        {

            /* If at least 1 new line has been found, start a new line in the buffered writer */
            while (t.getLine() > currentLine)
            {
                bw.newLine();                            // Start writing the tokens on the next line

                /* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
                currentLine++;
            }

            if (t.getChannel() == 0)
            {
                bw.append(t.getText());
            }

            if (t.getChannel() == 1)
            {
                bw.append(" ");
            }
        }
        /* Close the buffered writer */
        bw.close();
    }

}
