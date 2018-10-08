package uk.ac.warwick.dcs.sherlock.services.preprocessing;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.apache.commons.io.FilenameUtils;
import uk.ac.warwick.dcs.sherlock.FileTypes;

import java.io.*;
import java.util.List;

/**
 * @author Aliyah
 */
class JavaStrategy implements PreProcessingStrategy
{

    private static FileTypes setting;

    /**
     * The Java strategy Constructor
     *
     * @param setting - The pre-processing setting to be run
     *                <p>
     *                Where: 2	- No comments 3	- No comments no white space 4	- Comments 5 	- Tokenised
     *                <p>
     *                These values relate to the definitions of the enumeration SettingChoice in the settings class
     */
    public JavaStrategy(FileTypes setting)
    {
        this.setting = setting;
    }

    /* (non-Javadoc)
     * @see sherlock.model.analysis.preprocessing.PreProcessingStrategy#preProcessFiles()
     */
    @Override
    public void preProcessFiles(File[] filePaths, File targetDirectory)
    {

        switch (setting)
        {
            case NOC:
                for (File file : filePaths)
                {
                    String filename = FilenameUtils.removeExtension(file.getName());
                    String finalDestination = targetDirectory + File.separator + filename + ".txt";
                    FileInputStream fis = null;

                    try
                    {
                        /* Open the input file stream */
                        fis = new FileInputStream(file);

                        /* Create a CharStream that reads in the file */
                        CharStream input = CharStreams.fromStream(fis);

                        /* Create the Java Lexer and feed it the input */
                        JavaLexer lexer = new JavaLexer(input);

                        List<? extends Token> list = lexer.getAllTokens();
                        Vocabulary vocab = lexer.getVocabulary();

                        removeComments(list, vocab, finalDestination);

                        /* Close the input stream */
                        fis.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            case NCW:

                for (File file : filePaths)
                {

                    String filename = FilenameUtils.removeExtension(file.getName());
                    String finalDestination = targetDirectory + File.separator + filename + ".txt";
                    FileInputStream fis = null;

                    try
                    {
                        /* Open the input file stream */
                        fis = new FileInputStream(file);

                        /* Create a CharStream that reads in the file */
                        CharStream input = CharStreams.fromStream(fis);

                        /* Create the Java Lexer and feed it the input */
                        JavaLexer lexer = new JavaLexer(input);

                        List<? extends Token> list = lexer.getAllTokens();
                        Vocabulary vocab = lexer.getVocabulary();

                        removeComments_Whitespace(list, finalDestination);

                        /* Close the input stream */
                        fis.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                break;
            case COM:
                for (File file : filePaths)
                {
                    String filename = FilenameUtils.removeExtension(file.getName());
                    String finalDestination = targetDirectory + File.separator + filename + ".txt";
                    FileInputStream fis = null;

                    try
                    {
                        /* Open the input file stream */
                        fis = new FileInputStream(file);

                        /* Create a CharStream that reads in the file */
                        CharStream input = CharStreams.fromStream(fis);

                        /* Create the Java Lexer and feed it the input */
                        JavaLexer lexer = new JavaLexer(input);

                        List<? extends Token> list = lexer.getAllTokens();
                        Vocabulary vocab = lexer.getVocabulary();

                        extractComments(list, finalDestination);

                        /* Close the input stream */
                        fis.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                break;
            case TOK:
                //For each file to be parsed
                for (File file : filePaths)
                {
                    String filename = FilenameUtils.removeExtension(file.getName());
                    String finalDestination = targetDirectory + File.separator + filename + ".txt";
                    FileInputStream fis = null;
                    try
                    {
                        /* Open the input file stream */
                        fis = new FileInputStream(file);

                        /* Create a CharStream that reads in the file */
                        CharStream input = CharStreams.fromStream(fis);

                        /* Create the Java Lexer and feed it the input */
                        JavaLexer lexer = new JavaLexer(input);

                        List<? extends Token> list = lexer.getAllTokens();
                        Vocabulary vocab = lexer.getVocabulary();

                        tokenise(list, vocab, finalDestination);

                        /* Close the input stream */
                        fis.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                System.out.println("No settings");
        }

    }

    /**
     * Remove the comments without changing the format of the rest of the code
     *
     * @throws IOException
     */
    private void removeComments(List<? extends Token> list, Vocabulary vocab, String outputFile) throws IOException
    {
        /*Create a buffered writer to store all the tokens in a file */
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)));

        int currentLine = 1;
        for (Token t : list)
        {
            /* If at least 1 new line has been found, start a new line in the buffered writer */
            while (t.getLine() > currentLine)
            {
                bw.newLine();                            // Start writing the tokens on the next line
                currentLine++;
            }

            if (t.getChannel() == 0 || t.getChannel() == 2)
            {
                bw.append(t.getText());
            }
        }
        /* Close the buffered writer */
        bw.close();
    }

    /**
     * Write the comments to file
     *
     * @throws IOException
     */
    private void extractComments(List<? extends Token> list, String outputFile) throws IOException
    {
        /*Create a buffered writer to store all the tokens in a file */
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)));

        int currentLine = 1;
        for (Token t : list)
        {
            /* If at least 1 new line has been found, start a new line in the buffered writer */
            if (t.getLine() > currentLine)
            {
                bw.newLine();                            // Start writing the tokens on the next line

                /* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
                currentLine = t.getLine();                    // Set the currentLine to the line of the token
            }

            if (t.getChannel() == 1)
            {
                bw.append(t.getText());
            }
        }
        /* Close the buffered writer */
        bw.close();
    }

    /**
     * Tokenise the input and remove comments
     *
     * @throws IOException
     */
    private void tokenise(List<? extends Token> list, Vocabulary vocab, String outputFile) throws IOException
    {
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
                bw.append(vocab.getSymbolicName(t.getType()) + " ");
            }
        }
        /* Close the buffered writer */
        bw.close();

    }

    /**
     * Remove any unnecessary whitespace (e.g. \t and double spaces)
     *
     * @throws IOException
     */
    private void removeWhiteSpace(List<? extends Token> list) throws IOException
    {
        String destination = System.getProperty("user.home") + File.separator + "NoWhiteSpace.txt";

        /*Create a buffered writer to store all the tokens in a file */
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destination)));

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

            if (t.getChannel() == 0 | t.getChannel() == 1 | t.getChannel() == 2)
            {
                bw.append(t.getText());
            }
        }
        /* Close the buffered writer */
        bw.close();
    }

    /**
     * Remove the comments and unnecessary spaces and indentation
     *
     * @throws IOException
     */
    private void removeComments_Whitespace(List<? extends Token> list, String outputFile) throws IOException
    {
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

            if (t.getChannel() == 0 | t.getChannel() == 2)
            {
                bw.append(t.getText());
            }
        }
        /* Close the buffered writer */
        bw.close();
    }

    /**
     * Extract the white pattern, the tabs and spaces on each line
     *
     * @throws IOException
     */
    private void extractWhitePattern(List<? extends Token> list, Vocabulary vocab) throws IOException
    {
        String destination = System.getProperty("user.home") + File.separator + "WhitePattern.txt";

        /*Create a buffered writer to store all the tokens in a file */
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destination)));

        int currentLine = 1;
        for (Token t : list)
        {
            /* If at least 1 new line has been found, start a new line in the buffered writer */
            if (t.getLine() > currentLine)
            {
                bw.newLine();                            // Start writing the tokens on the next line

                /* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
                currentLine = t.getLine();                    // Set the currentLine to the line of the token
            }

            if (t.getChannel() == 2)
            {
                bw.append(vocab.getSymbolicName(t.getType()) + " ");
            }
        }
        /* Close the buffered writer */
        bw.close();
    }

    /**
     * A method to store the tokens and comments to respective directories. The new plain text files will be named after the original file.
     *
     * @param filepath        - Full Directory path to the file to be parsed
     * @param targetDirectory - /Sherlock/$DirectoryName$/preprocessing/
     */

    private void storeTokenisedResult(String filepath, String targetDirectory, boolean commentsIncluded)
    {
        String fileName = filepath.substring(filepath.lastIndexOf("/")) /*get name of file*/;
        /* Need to remove the .java extension and add .txt extension */

        //        File newFile = new File(fileName);
        FileInputStream fis = null;

        try
        {

            /* Open the input file stream */
            fis = new FileInputStream(filepath);

            /* Create a CharStream that reads in the file */
            CharStream input = CharStreams.fromStream(fis);

            /* Create the Java Lexer and feed it the input */
            JavaLexer lexer = new JavaLexer(input);

            /* Get all of the token definitions used by the lexer */
            Vocabulary vocab = lexer.getVocabulary();

            /* Get a list of all the tokens used in the input file */
            List<? extends Token> list = lexer.getAllTokens();

            String dest_toks = targetDirectory + File.separator + "Tokens" + File.separator + fileName /*.getName*/;
            File destination_toks = new File(dest_toks);

            /*Create a buffered writer to store all the tokens in a file */
            BufferedWriter bw_tokens = new BufferedWriter(new FileWriter(destination_toks));

            /* If comments are not to be written to file */
            if (!commentsIncluded)
            {
                /* For all tokens in the list, add them to the new file created */
                int currentLine = 1;
                for (Token t : list)
                {

                    /* If at least 1 new line has been found, start a new line in the buffered writer */
                    if (t.getLine() > currentLine)
                    {
                        bw_tokens.newLine();                            // Start writing the tokens on the next line

                        /* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
                        currentLine = t.getLine();                    // Set the currentLine to the line of the token
                    }

                    if (t.getChannel() == 0)
                    {
                        bw_tokens.append(vocab.getSymbolicName(t.getType()) + " ");
                    }
                }
                /* Close the buffered writer */
                bw_tokens.close();
            }
            else if (commentsIncluded)
            {                    /* If comments are to be written to file */
                String dest_coms = targetDirectory + File.separator + "Comments" + File.separator + fileName /*.getName*/;
                File destination_coms = new File(dest_coms);
                BufferedWriter bw_comments = new BufferedWriter(new FileWriter(destination_coms));

                /* For all tokens in the list, add them to the new file created */
                int currentLine = 1;
                for (Token t : list)
                {

                    /* If at least 1 new line has been found, start a new line in the buffered writer */
                    if (t.getLine() > currentLine)
                    {
                        bw_tokens.newLine();                            // Start writing the tokens on the next line
                        bw_comments.newLine();                        // Start writing the tokens on the next line

                        /* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
                        currentLine = t.getLine();                    // Set the currentLine to the line of the token
                    }

                    if (t.getChannel() == 0)
                    {
                        bw_tokens.append(vocab.getSymbolicName(t.getType()));
                        bw_tokens.append(" ");
                    }
                    else if (t.getChannel() == 1)
                    {
                        bw_comments.append(t.getText() + " ");
                    }
                }
                /* Close the buffered writer */
                bw_tokens.close();
                bw_comments.close();
            }

            /* Close the input stream */
            fis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void storeTree(String filepath, File targetDirectory)
    {
        System.out.println("Storing Tree");
        System.out.println("File: " + filepath);
        System.out.println("Target: " + targetDirectory);

    }
}
