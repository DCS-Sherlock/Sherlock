/**
 * 
 */
package sherlock.model.analysis.preprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;

/**
 * @author Aliyah
 *
 */
class JavaStrategy implements PreProcessingStrategy {

	/* (non-Javadoc)
	 * @see sherlock.model.analysis.preprocessing.PreProcessingStrategy#preProcessFiles()
	 */
	@Override
	public void preProcessFiles(String[] filePaths, String targetDirectory) {
		//For each file to be parsed
		for( String filepath : filePaths ) { 
			// Store the Tokens extracted from the file in the target directory
			storeTokenisedResult(filepath, targetDirectory);
			// Store the LISP-Style Tree
			// storeTree(sourceDirectory, file, targetDirectory);
		}
	}
	
	/**  
	 * A method to store the tokens and comments to respective directories. 
	 * The new plain text files will be named after the original file.
	 * 
	 * @param filepath			- Full Directory path to the file to be parsed
	 * @param targetDirectory 	- /Sherlock/$DirectoryName$/preprocessing/
	 */
	private void storeTokenisedResult(String filepath, String targetDirectory) {
		System.out.println("Storing Tokens");
		System.out.println("File: " + filepath);
		System.out.println("Target: " + targetDirectory);
		
		String fileName = filepath.substring( filepath.lastIndexOf("/") ) /*get name of file*/;
		/* Need to remove the .java extension and add .txt extension */
		System.out.println("File name " + fileName);
//        File newFile = new File(fileName);
        FileInputStream fis = null;
        
        try {
        		
        		/* Open the input file stream */
        		fis = new FileInputStream(filepath);
        		
        		/* Create a CharStream that reads in the file */
        		ANTLRInputStream input = new ANTLRInputStream(fis);
        		
        		/* Create the Java Lexer and feed it the input */
        		JavaLexer lexer = new JavaLexer(input);
      
        		/* Get all of the token definitions used by the lexer */
        		Vocabulary vocab = lexer.getVocabulary();
        		
        		/* Get a list of all the tokens used in the input file */
        		List<? extends Token> list = lexer.getAllTokens();
        		
        		String dest_toks = targetDirectory + File.separator + "Tokens" + File.separator + fileName /*.getName*/ ;
        		File destination_toks = new File( dest_toks );
        		
        		String dest_coms = targetDirectory + File.separator + "Comments" + File.separator + fileName /*.getName*/ ;
        		File destination_coms = new File( dest_coms );
        		
        		/*Create a buffered writer to store all the tokens in a file */
        		BufferedWriter bw_tokens = new BufferedWriter(new FileWriter(destination_toks));
        		BufferedWriter bw_comments = new BufferedWriter(new FileWriter(destination_coms));
        		
        		/* For all tokens in the list, add them to the new file created */
        		int currentLine = 1 ;
        		for( Token t : list ) {
        			int lineDifference = t.getLine() - currentLine;
        			while ( lineDifference != 0 ) {
        				currentLine++;
        				bw_tokens.newLine();								// Start writing the tokens on the next line if they appear
        				bw_comments.newLine();							// Start writing the comments on the next line if they appear
        				lineDifference = t.getLine() - currentLine;
        			}

        			if ( t.getChannel() == 0 ) {
        				bw_tokens.append(vocab.getSymbolicName(t.getType()));
        				bw_tokens.append(" ");
        			} else if ( t.getChannel() == 1) {
        				bw_comments.append(t.getText());
        				bw_comments.append(" ");
        			}
        		}
        		
        		
        		/* Close the buffered writer */
        		bw_tokens.close();
        		bw_comments.close();
        		
        		/* Close the input stream */
	    		fis.close();
        } catch (IOException e) {
        		e.printStackTrace();
        }
		
	}
	
	private void storeTree(String filepath, File targetDirectory) {
		System.out.println("Storing Tree");
		System.out.println("File: " + filepath);
		System.out.println("Target: " + targetDirectory);
		
		
	}

}
