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
	
	private List<Boolean> settings ;
	
	/**
	 * Constructor for the JavaStrategy
	 * @param commentsIncluded		- Boolean to indicating whether the comments should be written to file or not
	 */
	public JavaStrategy(File[] filePaths, File targetDirectory, List<Boolean> settings) {
		this.settings = settings;
		preProcessFiles(filePaths, targetDirectory);
	}
	
	/* (non-Javadoc)
	 * @see sherlock.model.analysis.preprocessing.PreProcessingStrategy#preProcessFiles()
	 */
	@Override
	public void preProcessFiles(File[] filePaths, File targetDirectory ) {
		
			//For each file to be parsed
			for( File filepath : filePaths ) { 
				// Call the pre-processing method depending on settings
				
			}
	}
	
//	private List<Integer> getSettings() {
//		return settings;
//	}
	
	/**
	 * Remove the comments without changing the format of the rest of the code
	 * @throws IOException 
	 */
	private void removeComments(List<? extends Token> list, Vocabulary vocab) throws IOException{
		System.out.println("Remove Comments");
		
		String destination = System.getProperty("user.home") + File.separator + "noComments.txt" ;
		
		/*Create a buffered writer to store all the tokens in a file */
		BufferedWriter bw = new BufferedWriter(new FileWriter( new File( destination )));
		
		int currentLine = 1 ;
		for( Token t : list ) {
			/* If at least 1 new line has been found, start a new line in the buffered writer */
			while ( t.getLine() > currentLine) {
				bw.newLine();							// Start writing the tokens on the next line
				currentLine++;	 
			}
			
			if ( t.getChannel() == 0 || t.getChannel() == 2 ) {
				bw.append(t.getText());
			}
		}
		/* Close the buffered writer */
    		bw.close();
	}

	/**
	 * Write the comments to file 
	 * @throws IOException 
	 */
	private void extractComments(List<? extends Token> list) throws IOException{
		System.out.println("Extract Comments");
		String destination = System.getProperty("user.home") + File.separator + "Comments.txt" ;
		
		/*Create a buffered writer to store all the tokens in a file */
		BufferedWriter bw = new BufferedWriter(new FileWriter( new File( destination )));
		
		int currentLine = 1 ;
		for( Token t : list ) {
			/* If at least 1 new line has been found, start a new line in the buffered writer */
			if ( t.getLine() > currentLine) {
				bw.newLine();							// Start writing the tokens on the next line
				
				/* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
				currentLine = t.getLine();					// Set the currentLine to the line of the token 
			}
			
			if ( t.getChannel() == 1 ) {
				bw.append(t.getText());
			}
		}
		/* Close the buffered writer */
    		bw.close();
	}

	/**
	 * Tokenise the input and remove comments
	 * @throws IOException 
	 */
	private void tokenise(List<? extends Token> list, Vocabulary vocab) throws IOException{
		System.out.println("Tokenise");
		
		String destination = System.getProperty("user.home") + File.separator + "Tokens.txt" ;
		
		/*Create a buffered writer to store all the tokens in a file */
		BufferedWriter bw = new BufferedWriter(new FileWriter( new File( destination )));
		
		/* For all tokens in the list, add them to the new file created */
		int currentLine = 1 ;
		for( Token t : list ) {

			/* If at least 1 new line has been found, start a new line in the buffered writer */
			while ( t.getLine() > currentLine) {
				bw.newLine();							// Start writing the tokens on the next line
				
				/* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
				currentLine++; 
			}
			
			if ( t.getChannel() == 0 ) {
				bw.append(vocab.getSymbolicName(t.getType()) + " ");
			}
		}
		/* Close the buffered writer */
    		bw.close();
		
	} 

	/**
	 * Remove any unnecessary whitespace (e.g. \t and double spaces)
	 * @throws IOException 
	 */
	private void removeWhiteSpace(List<? extends Token> list) throws IOException{
		System.out.println("Remove WS ");
		String destination = System.getProperty("user.home") + File.separator + "NoWhiteSpace.txt" ;
		
		/*Create a buffered writer to store all the tokens in a file */
		BufferedWriter bw = new BufferedWriter(new FileWriter( new File( destination )));
		
		/* For all tokens in the list, add them to the new file created */
		int currentLine = 1 ;
		for( Token t : list ) {

			/* If at least 1 new line has been found, start a new line in the buffered writer */
			while ( t.getLine() > currentLine) {
				bw.newLine();							// Start writing the tokens on the next line
				
				/* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
				currentLine++; 
			}
			
			if ( t.getChannel() == 0 | t.getChannel() == 1 | t.getChannel() == 2) {
				bw.append(t.getText());
			}
		}
		/* Close the buffered writer */
    		bw.close();
	}

	/**
	 * Remove the comments and unnecessary spaces and indentation
	 * @throws IOException 
	 */
	private void removeComments_WhiteSpace(List<? extends Token> list) throws IOException{
		System.out.println("Remove WS and Comments");
		String destination = System.getProperty("user.home") + File.separator + "NoWS_Comments.txt" ;
		
		/*Create a buffered writer to store all the tokens in a file */
		BufferedWriter bw = new BufferedWriter(new FileWriter( new File( destination )));
		
		/* For all tokens in the list, add them to the new file created */
		int currentLine = 1 ;
		for( Token t : list ) {

			/* If at least 1 new line has been found, start a new line in the buffered writer */
			while ( t.getLine() > currentLine) {
				bw.newLine();							// Start writing the tokens on the next line
				
				/* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
				currentLine++; 
			}
			
			if ( t.getChannel() == 0 | t.getChannel() == 2) {
				bw.append(t.getText());
			}
		}
		/* Close the buffered writer */
    		bw.close();
	}

	/**
	 * Extract the white pattern, the tabs and spaces on each line
	 * @throws IOException 
	 */
	private void extractWhitePattern(List<? extends Token> list, Vocabulary vocab) throws IOException{
		System.out.println("Extract WS Pattern");
		String destination = System.getProperty("user.home") + File.separator + "WhitePattern.txt" ;
		
		/*Create a buffered writer to store all the tokens in a file */
		BufferedWriter bw = new BufferedWriter(new FileWriter( new File( destination )));
		
		int currentLine = 1 ;
		for( Token t : list ) {
			/* If at least 1 new line has been found, start a new line in the buffered writer */
			if ( t.getLine() > currentLine) {
				bw.newLine();							// Start writing the tokens on the next line
				
				/* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
				currentLine = t.getLine();					// Set the currentLine to the line of the token 
			}
			
			if ( t.getChannel() == 2 ) {
				bw.append(vocab.getSymbolicName(t.getType()) + " ");
			}
		}
		/* Close the buffered writer */
    		bw.close();
	}


	/**  
	 * A method to store the tokens and comments to respective directories. 
	 * The new plain text files will be named after the original file.
	 * 
	 * @param filepath			- Full Directory path to the file to be parsed
	 * @param targetDirectory 	- /Sherlock/$DirectoryName$/preprocessing/
	 */
	
	private void storeTokenisedResult(String filepath, String targetDirectory, boolean commentsIncluded) {
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
        		
        		/*Create a buffered writer to store all the tokens in a file */
        		BufferedWriter bw_tokens = new BufferedWriter(new FileWriter(destination_toks));
        		
        		/* If comments are not to be written to file */
        		if ( !commentsIncluded ){
        			/* For all tokens in the list, add them to the new file created */
            		int currentLine = 1 ;
            		for( Token t : list ) {
            			
            			/* If at least 1 new line has been found, start a new line in the buffered writer */
            			if ( t.getLine() > currentLine) {
            				bw_tokens.newLine();							// Start writing the tokens on the next line
            				
            				/* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
            				currentLine = t.getLine();					// Set the currentLine to the line of the token 
            			}
            			
            			if ( t.getChannel() == 0 ) {
            				bw_tokens.append(vocab.getSymbolicName(t.getType()) + " ");
            			}
            		}
            		/* Close the buffered writer */
            		bw_tokens.close();
        		} else if ( commentsIncluded ) {					/* If comments are to be written to file */
	        		String dest_coms = targetDirectory + File.separator + "Comments" + File.separator + fileName /*.getName*/ ;
	        		File destination_coms = new File( dest_coms );
	        		BufferedWriter bw_comments = new BufferedWriter(new FileWriter(destination_coms));
	        		
	        		/* For all tokens in the list, add them to the new file created */
            		int currentLine = 1 ;
            		for( Token t : list ) {
            			
            			/* If at least 1 new line has been found, start a new line in the buffered writer */
            			if ( t.getLine() > currentLine) {
            				bw_tokens.newLine();							// Start writing the tokens on the next line
            				bw_comments.newLine();						// Start writing the tokens on the next line
            				
            				/* When the token is a multiple line comment, more than one line can be found so we can't increment the value of currentLine */
            				currentLine = t.getLine();					// Set the currentLine to the line of the token 
            			}
            			
            			if ( t.getChannel() == 0 ) {
            				bw_tokens.append(vocab.getSymbolicName(t.getType()));
            				bw_tokens.append(" ");
            			} else if ( t.getChannel() == 1) {
            				bw_comments.append(t.getText() + " ");
            			}            			
            		}
            		/* Close the buffered writer */
            		bw_tokens.close();
            		bw_comments.close();
        		}
        		
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
