/**
 * 
 */
package sherlock.model.analysis.detection;

import java.io.File;

import sherlock.fileSystem.DirectoryProcessor;
import sherlock.fileSystem.filters.*;
import sherlock.model.analysis.FileTypes;
import sherlock.model.analysis.Settings;

/**
 * @author Aliyah
 *
 */
public class DetectionHandler {
	private Settings s ; 

	/**
	 * DectionHandler constructor
	 * This constructor initiates the detection strategies by calling the runDetectionStrategies method.
	 *
	 * @param s 		- The file types requested by the user for this detection
	 */
	public DetectionHandler(Settings s){
		this.s = s ;
		
		runDetectionStrategies();
	}
	
	private void runDetectionStrategies() {
		System.out.println("----Running detection strategy----");
		
		if ( s.getOriginalProfile().isInUse() ) {
			System.out.println("Original Detection");
			
			/* Get the files located in the Original Directory */
			File originalDirectory = new File (s.getOriginalProfile().getOutputDir() );
			System.out.println(originalDirectory.getPath());
			
			/* 
			 * Original File can be either source code or plain text, so we must 
			 * filter the two before performing detection
			 */
			
			DirectoryProcessor text = new DirectoryProcessor(originalDirectory, new PlainTextFilter());
			File[] plainTextFiles = text.getInputFiles();
			
			DirectoryProcessor sourceCode = new DirectoryProcessor(originalDirectory, new JavaFileFilter());
			File[] sourceCodeFiles = sourceCode.getInputFiles();
			
			NGramsStrategy ng = new NGramsStrategy(sourceCodeFiles, s.getOriginalProfile());
		}
		
		if ( s.getNoWSProfile().isInUse() ) {
			System.out.println("No WS Detection");
			/* Get the files located in the NoWhitespace Directory */
			File noWSDirectory = new File (s.getNoWSProfile().getOutputDir() );
			System.out.println(noWSDirectory.getPath());
			File[] noWSFiles = noWSDirectory.listFiles();
		}
		
		if ( s.getNoCommentsProfile().isInUse() ) {
			System.out.println("No Comments Detection");
			File noCommentsDirectory = new File (s.getNoCommentsProfile().getOutputDir() );
			System.out.println(noCommentsDirectory.getPath());
			File[] noCommentsFiles = noCommentsDirectory.listFiles();
		}
		
		if ( s.getNoCWSProfile().isInUse() ) {
			System.out.println("No Comments, No WS Detection");
			File noCWSDirectory = new File (s.getNoCWSProfile().getOutputDir() );
			System.out.println(noCWSDirectory.getPath());
			File[] noCWSFiles = noCWSDirectory.listFiles();
		}
		
		if ( s.getCommentsProfile().isInUse() ) {
			System.out.println("Comments Detection");
			File commentsDirectory = new File (s.getCommentsProfile().getOutputDir() );
			System.out.println(commentsDirectory.getPath());
			File[] commentsFiles = commentsDirectory.listFiles();
		}
		
		if ( s.getTokenisedProfile().isInUse() ) {
			System.out.println("Token Detection");
			File tokDirectory = new File (s.getTokenisedProfile().getOutputDir() );
			System.out.println(tokDirectory.getPath());
			File[] tokFiles = tokDirectory.listFiles();
		}
		
		if ( s.getWSPatternProfile().isInUse() ) {
			System.out.println("WS Pattern Detection");
			File wsPatternDirectory = new File (s.getWSPatternProfile().getOutputDir() );
			System.out.println(wsPatternDirectory.getPath());
			File[] wsPatternFiles = wsPatternDirectory.listFiles();
		}
	}
}
