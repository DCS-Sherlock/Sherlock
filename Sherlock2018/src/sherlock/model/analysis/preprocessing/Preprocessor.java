package sherlock.model.analysis.preprocessing;

import java.io.File;
import java.util.List;

import sherlock.FileSystem.AcceptedFileFilter;
import sherlock.FileSystem.DirectoryProcessor;
import sherlock.FileSystem.JavaFileFilter;
import sherlock.model.analysis.SettingProfile;

/**
 * Determines which pre-processing technique is to be used a result of the Setting profile properties.
 * 
 * @author Aliyah
 *
 */
public class Preprocessor {
	
	private Settings s ; 
	/**
	 * Pre-processor Constructor
	 */
	public Preprocessor(Settings s){
		this.s = s ;
		
		selectPreprocessingStrategies();
	}
	
	private void selectPreprocessingStrategies() {
		/*
		 * If original is to be used - do nothing
		 * 
		 * If No Whitespace - call the no whitespace strategy
		 * 
		 * If No Comments - Java strategy
		 * 
		 * If No Comments and No Whitespace
		 * 
		 * If Comments - Java strategy
		 * 
		 * If Tokenised - Java strategy
		 * 
		 * If Whitespace pattern - call the whitespace pattern strategy
		 * */
		
		List<Boolean> statuses = s.getInUseStatus();
		
		System.out.println("------------------");
		if ( s.getOriginalProfile().isInUse() ) {
			System.out.println("Original Pre-processing");
			
		}
		
		
		if ( s.getNoWSProfile().isInUse() ) {
			System.out.println("No WS Pre-processing");
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			String targetDirectory = s.getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + s.getNoWSProfile().getOutputDir() ;
			
			System.out.println(targetDirectory);
			
			File target = new File (targetDirectory);
			if ( target.exists() && target.isDirectory() ) {
				System.out.println("The target exists");
			} else {
				target.mkdir();
			}
				
			PreProcessingContext noWS = new PreProcessingContext(new NoWhiteSpaceStrategy(), filePaths , target );
			
		}
		
		if ( s.getNoCommentsProfile().isInUse() ) {
			System.out.println("No Comments Pre-processing");
		}
		
		if ( s.getNoCWSProfile().isInUse() ) {
			System.out.println("No Comments, No WS Pre-processing");
		}
		
		if ( s.getCommentsProfile().isInUse() ) {
			System.out.println("Comments Pre-processing");
		}
		
		if ( s.getTokenisedProfile().isInUse() ) {
			System.out.println("Token Pre-processing");
		}
		
		if ( s.getWSPatternProfile().isInUse() ) {
			System.out.println("WS Pattern Pre-processing");
		}
	}
	

	
}
