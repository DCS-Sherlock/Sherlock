package uk.ac.warwick.dcs.sherlock.services.preprocessing;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import uk.ac.warwick.dcs.sherlock.services.fileSystem.DirectoryProcessor;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.*;
import uk.ac.warwick.dcs.sherlock.FileTypes;
import uk.ac.warwick.dcs.sherlock.SettingProfile;
import uk.ac.warwick.dcs.sherlock.Settings;

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
		
		s.getInUseStatus();
		
		System.out.println("------------------");
		if ( s.getOriginalProfile().isInUse() ) {
			System.out.println("Original Pre-processing");
			String targetDirectory;
			if (!s.getOriginalProfile().isOutputDirSet()) {
				s.getOriginalProfile().setIsOutputDirSet(true);
				targetDirectory = s.getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + s.getOriginalProfile().getOutputDir() ;
			}else {
				targetDirectory = s.getOriginalProfile().getOutputDir();
			}
			s.getOriginalProfile().setOutputDir(targetDirectory);
		}
		
		if ( s.getNoWSProfile().isInUse() ) {
			System.out.println("No WS Pre-processing");
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			File target = getTargetDir(s.getNoWSProfile(), s.getSourceDirectory());
			
			s.getNoWSProfile().setOutputDir(target.getAbsolutePath());
				
			new PreProcessingContext(new NoWhiteSpaceStrategy(), filePaths , target );
		}
		
		if ( s.getNoCommentsProfile().isInUse() ) {
			System.out.println("No Comments Pre-processing");
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			File target = getTargetDir(s.getNoCommentsProfile(), s.getSourceDirectory());
			
			s.getNoCommentsProfile().setOutputDir(target.getAbsolutePath());
			
			new PreProcessingContext(new JavaStrategy( FileTypes.NOC ), filePaths , target );
		}
		
		if ( s.getNoCWSProfile().isInUse() ) {
			System.out.println("No Comments, No WS Pre-processing");
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			File target = getTargetDir(s.getNoCWSProfile(), s.getSourceDirectory());
			s.getNoCWSProfile().setOutputDir(target.getAbsolutePath());
			
			new PreProcessingContext(new JavaStrategy( FileTypes.NCW ), filePaths , target );
		}
		
		if ( s.getCommentsProfile().isInUse() ) {
			System.out.println("Comments Pre-processing");
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			File target = getTargetDir(s.getCommentsProfile(), s.getSourceDirectory());
			
			s.getCommentsProfile().setOutputDir(target.getAbsolutePath());
			new PreProcessingContext(new JavaStrategy( FileTypes.COM ), filePaths , target );
		}
		
		if ( s.getTokenisedProfile().isInUse() ) {
			System.out.println("Token Pre-processing");
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			File target = getTargetDir(s.getTokenisedProfile(), s.getSourceDirectory());
			
			s.getTokenisedProfile().setOutputDir(target.getAbsolutePath());
			new PreProcessingContext(new JavaStrategy( FileTypes.TOK ), filePaths , target );
			
		}
		
		if ( s.getWSPatternProfile().isInUse() ) {
			System.out.println("WS Pattern Pre-processing");
			
			DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter() );
			File[] filePaths = dp.getInputFiles();
			
			File target = getTargetDir(s.getWSPatternProfile(), s.getSourceDirectory());
			
			s.getWSPatternProfile().setOutputDir(target.getAbsolutePath());
			new PreProcessingContext(new WhitespacePatternStrategy(), filePaths , target );
		}
		
		System.out.println("Pre-processing complete");
		s.setPreprocessingStatus(true);
	}
	
	private File makeDirectory(String targetDirectory) {
		
		File target = new File (targetDirectory);
		if ( target.exists() && target.isDirectory() ) {
			System.out.println("The target exists");
		} else {
			target.mkdir();
		}
		return target;
	}
	private File getTargetDir(SettingProfile profile, File sourceDir) {
		String targetDirectory;
		if (!profile.isOutputDirSet()) {
			profile.setIsOutputDirSet(true);
			targetDirectory = sourceDir.getAbsolutePath() + File.separator + "Preprocessing" + File.separator + profile.getOutputDir();
		}else {
			targetDirectory = profile.getOutputDir();
		}

		File target = makeDirectory(targetDirectory);
		return target;
	}
	
}
