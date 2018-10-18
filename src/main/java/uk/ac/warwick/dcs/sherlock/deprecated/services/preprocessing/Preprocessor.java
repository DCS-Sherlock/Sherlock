package uk.ac.warwick.dcs.sherlock.deprecated.services.preprocessing;

import uk.ac.warwick.dcs.sherlock.deprecated.FileTypes;
import uk.ac.warwick.dcs.sherlock.deprecated.SettingProfile;
import uk.ac.warwick.dcs.sherlock.deprecated.Settings;
import uk.ac.warwick.dcs.sherlock.deprecated.services.fileSystem.DirectoryProcessor;
import uk.ac.warwick.dcs.sherlock.deprecated.services.fileSystem.filters.*;

import java.io.File;

/**
 * Determines which pre-processing technique is to be used a result of the Setting profile properties.
 *
 * @author Aliyah
 */
public class Preprocessor {

	private Settings s;

	/**
	 * Pre-processor constructor. This constructor initiates the pre-processing strategies by calling the runPreprocessingStrategies method.
	 *
	 * @param s - The file types requested by the user for this detection
	 */
	public Preprocessor(Settings s) {
		this.s = s;
		runPreprocessingStrategies();
	}

	private void runPreprocessingStrategies() {
		/*
		 * If original is to be used - do nothing
		 *
		 * otherwise call java strategy
		 * */

		s.getInUseStatus();

		if (s.getOriginalProfile().isInUse()) {
			String targetDirectory;
			if (!s.getOriginalProfile().isOutputDirSet()) {
				s.getOriginalProfile().setIsOutputDirSet(true);
				targetDirectory = s.getSourceDirectory().getAbsolutePath() + File.separator + "Preprocessing" + File.separator + s.getOriginalProfile().getOutputDir();
			}
			else {
				targetDirectory = s.getOriginalProfile().getOutputDir();
			}
			s.getOriginalProfile().setOutputDir(targetDirectory);
		}
		if (s.getNoWSProfile().isInUse()) {
			File[] filePaths = getFilePaths(s);
			File target = getTargetDir(s.getNoWSProfile(), s.getSourceDirectory());
			s.getNoWSProfile().setOutputDir(target.getAbsolutePath());
			new PreProcessingContext(new JavaStrategy(FileTypes.NWS), filePaths, target);
		}
		if (s.getNoCommentsProfile().isInUse()) {
			File[] filePaths = getFilePaths(s);
			File target = getTargetDir(s.getNoCommentsProfile(), s.getSourceDirectory());
			s.getNoCommentsProfile().setOutputDir(target.getAbsolutePath());
			new PreProcessingContext(new JavaStrategy(FileTypes.NOC), filePaths, target);
		}
		if (s.getNoCWSProfile().isInUse()) {
			File[] filePaths = getFilePaths(s);
			File target = getTargetDir(s.getNoCWSProfile(), s.getSourceDirectory());
			s.getNoCWSProfile().setOutputDir(target.getAbsolutePath());
			new PreProcessingContext(new JavaStrategy(FileTypes.NCW), filePaths, target);
		}
		if (s.getCommentsProfile().isInUse()) {
			File[] filePaths = getFilePaths(s);
			File target = getTargetDir(s.getCommentsProfile(), s.getSourceDirectory());
			s.getCommentsProfile().setOutputDir(target.getAbsolutePath());
			new PreProcessingContext(new JavaStrategy(FileTypes.COM), filePaths, target);
		}
		if (s.getTokenisedProfile().isInUse()) {
			File[] filePaths = getFilePaths(s);
			File target = getTargetDir(s.getTokenisedProfile(), s.getSourceDirectory());
			s.getTokenisedProfile().setOutputDir(target.getAbsolutePath());
			new PreProcessingContext(new JavaStrategy(FileTypes.TOK), filePaths, target);
		}
		if (s.getWSPatternProfile().isInUse()) {
			File[] filePaths = getFilePaths(s);
			File target = getTargetDir(s.getWSPatternProfile(), s.getSourceDirectory());
			s.getWSPatternProfile().setOutputDir(target.getAbsolutePath());
			new PreProcessingContext(new JavaStrategy(FileTypes.WSP), filePaths, target);
		}
		s.setPreprocessingStatus(true);
	}

	private File[] getFilePaths(Settings s) {
		DirectoryProcessor dp = new DirectoryProcessor(s.getOriginalDirectory(), new JavaFileFilter());
		return dp.getInputFiles();
	}

	private File getTargetDir(SettingProfile profile, File sourceDir) {
		String targetDirectory;
		if (!profile.isOutputDirSet()) {
			profile.setIsOutputDirSet(true);
			targetDirectory = sourceDir.getAbsolutePath() + File.separator + "Preprocessing" + File.separator + profile.getOutputDir();
		}
		else {
			targetDirectory = profile.getOutputDir();
		}
		return makeDirectory(targetDirectory);
	}

	private File makeDirectory(String targetDirectory) {
		File target = new File(targetDirectory);
		if (!target.exists() || !target.isDirectory()) {
			target.mkdir();
		}
		return target;
	}

}
