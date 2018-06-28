/**
 * 
 */
package uk.ac.warwick.dcs.sherlock.services.detection;

import java.io.File;
import java.util.ArrayList;

import uk.ac.warwick.dcs.sherlock.services.fileSystem.DirectoryProcessor;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.*;

import uk.ac.warwick.dcs.sherlock.FileTypes;
import uk.ac.warwick.dcs.sherlock.Settings;

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
			File parent = originalDirectory.getParentFile().getParentFile();
			String targetDirectory = parent.getAbsolutePath() + File.separator + "Report" + File.separator + "Original";
			/* 
			 * Original File can be either source code or plain text, so we must 
			 * filter the two before performing detection
			 */
			File target = new File (targetDirectory);
			if ( target.exists() && target.isDirectory() ) {
				System.out.println("The target exists");
			} else {
				target.mkdir();
			}
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
			System.out.println("***************No Comments Detection");
			File noCommentsDirectory = new File (s.getNoCommentsProfile().getOutputDir() );
			System.out.println(noCommentsDirectory.getPath());
			File[] noCommentsFiles = noCommentsDirectory.listFiles();
			for (int i=0; i < noCommentsFiles.length; i++) {
				System.out.println(noCommentsFiles[i].toString());
			}
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
