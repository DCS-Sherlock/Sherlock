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

import org.apache.commons.io.FilenameUtils;

public class NoWhiteSpaceStrategy implements PreProcessingStrategy {

	@Override
	public void preProcessFiles(File[] filePaths, File targetDirectory) {
		System.out.println("Removing White Space");
				
		for( File file : filePaths) {
			System.out.println("File " + file.getAbsolutePath() );
			
			String filename = FilenameUtils.removeExtension(file.getName());
			String finalDestination = targetDirectory + File.separator + filename + ".txt";
			System.out.println("finalDestination " + finalDestination);
//			String fileName = System.getProperty("user.home") + "/test_Zip/helloWorld.java";
//	        File file = new File(fileName);
	        FileInputStream fis = null;
	        
	        try {
		        	/* Open the input file stream */
		    		fis = new FileInputStream(file);
		    		
		    		/* Create a CharStream that reads in the file */
		        	ANTLRInputStream input = new ANTLRInputStream(fis);
		        		
		        	/* Create the Java Lexer and feed it the input */
		        NowhitespaceLexer lexer = new NowhitespaceLexer(input);
	       
		        	List<? extends Token> list = lexer.getAllTokens();
		        	Vocabulary vocab = lexer.getVocabulary();
		        	
		        	
		        	extractWhitePattern(list, finalDestination);
		        	
		        	/* Close the input stream */
		    		fis.close();    	
	        } catch (IOException e) {
	    			e.printStackTrace();
	        }
		}
	}
	
	/**
	 * Remove any unnecessary whitespace (e.g. \t and double spaces)
	 * @throws IOException 
	 */
	public static void extractWhitePattern(List<? extends Token> list, String outputFile) throws IOException{
		System.out.println("Remove WS ");
		System.out.println("Target File: " + outputFile);
//		String destination = System.getProperty("user.home") + File.separator + "Result" + File.separator + "NoWhiteSpace.txt" ;
		
		/*Create a buffered writer to store all the tokens in a file */
		BufferedWriter bw = new BufferedWriter(new FileWriter( new File( outputFile )));
		
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
				bw.append(t.getText());
			}
			
			if ( t.getChannel() == 1 ) {
				bw.append(" ");
			}
		}
		/* Close the buffered writer */
    		bw.close();
	}

}
