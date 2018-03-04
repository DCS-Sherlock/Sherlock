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
	 * Pre-processor constructor.
	 * This constructor initiates the pre-processing strategies by calling the runPreprocessingStrategies method.
	 *
	 * @param s 		- The file types requested by the user for this detection
	 */
	public Preprocessor(Settings s){
		this.s = s ;
		
		runPreprocessingStrategies();
	}
	
	private void runPreprocessingStrategies() {
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
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			String targetDirectory = s.getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + s.getNoCommentsProfile().getOutputDir() ;
			
			System.out.println("Target directory: \t" +targetDirectory);
			
			File target = new File (targetDirectory);
			if ( target.exists() && target.isDirectory() ) {
				System.out.println("The target exists");
			} else {
				target.mkdir();
			}
			
			PreProcessingContext noComments = new PreProcessingContext(new JavaStrategy( FileTypes.NOC.getValue() ), filePaths , target );
		}
		
		if ( s.getNoCWSProfile().isInUse() ) {
			System.out.println("No Comments, No WS Pre-processing");
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			String targetDirectory = s.getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + s.getNoCWSProfile().getOutputDir() ;
			
			System.out.println("Target directory: \t" +targetDirectory);
			
			File target = new File (targetDirectory);
			if ( target.exists() && target.isDirectory() ) {
				System.out.println("The target exists");
			} else {
				target.mkdir();
			}
			
			PreProcessingContext noCWS = new PreProcessingContext(new JavaStrategy( FileTypes.NCW.getValue() ), filePaths , target );
		}
		
		if ( s.getCommentsProfile().isInUse() ) {
			System.out.println("Comments Pre-processing");
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			String targetDirectory = s.getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + s.getCommentsProfile().getOutputDir() ;
			
			System.out.println("Target directory: \t" +targetDirectory);
			
			File target = new File (targetDirectory);
			if ( target.exists() && target.isDirectory() ) {
				System.out.println("The target exists");
			} else {
				target.mkdir();
			}
			
			PreProcessingContext comments = new PreProcessingContext(new JavaStrategy( FileTypes.COM.getValue() ), filePaths , target );
		}
		
		if ( s.getTokenisedProfile().isInUse() ) {
			System.out.println("Token Pre-processing");
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			String targetDirectory = s.getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + s.getTokenisedProfile().getOutputDir() ;
			
			System.out.println("Target directory: \t" +targetDirectory);
			
			File target = new File (targetDirectory);
			if ( target.exists() && target.isDirectory() ) {
				System.out.println("The target exists");
			} else {
				target.mkdir();
			}
			
			PreProcessingContext tokens = new PreProcessingContext(new JavaStrategy( FileTypes.TOK.getValue() ), filePaths , target );
			
		}
		
		if ( s.getWSPatternProfile().isInUse() ) {
			System.out.println("WS Pattern Pre-processing");
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			String targetDirectory = s.getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + s.getWSPatternProfile().getOutputDir() ;
			
			System.out.println("Target directory: \t" +targetDirectory);
			
			File target = new File (targetDirectory);
			if ( target.exists() && target.isDirectory() ) {
				System.out.println("The target exists");
			} else {
				target.mkdir();
			}
			
			PreProcessingContext wsPattern = new PreProcessingContext(new WhitespacePatternStrategy(), filePaths , target );
		}
	}
	

	
}
