/**
 *
 */
package uk.ac.warwick.dcs.sherlock.deprecated.services.detection;

import uk.ac.warwick.dcs.sherlock.deprecated.Settings;
import uk.ac.warwick.dcs.sherlock.deprecated.services.fileSystem.DirectoryProcessor;
import uk.ac.warwick.dcs.sherlock.deprecated.services.fileSystem.filters.*;

import java.io.File;
import java.util.*;

/**
 * @author Aliyah
 */
public class DetectionHandler {

	private Settings userSettings;
	private int ngramlength;
	private DetectionStrategy strategy;

	/**
	 * DectionHandler constructor This constructor initiates the detection strategies by calling the runDetectionStrategies method.
	 *
	 * @param userSettings     - The file types requested by the user for this detection
	 * @param ngramLength      - The minimum length an Ngram has be to be considered a run
	 * @param concreteStrategy - The detection strategy that will be used in this detection handler
	 */
	public DetectionHandler(Settings userSettings, int ngramLength, DetectionStrategy concreteStrategy) {
		this.ngramlength = ngramLength;
		this.userSettings = userSettings;
		this.strategy = concreteStrategy;

	}

	public ArrayList<MyEdge> runDetectionStrategies() {
		System.out.println("----Running detection strategy----");
		ArrayList<MyEdge> edges;
		if (userSettings.getOriginalProfile().isInUse()) {
			System.out.println("Original Detection");

			/* Get the files located in the Original Directory */
			File originalDirectory = new File(userSettings.getOriginalProfile().getOutputDir());
			File parent = originalDirectory.getParentFile().getParentFile();
			String targetDirectory = parent.getAbsolutePath() + File.separator + "Report" + File.separator + "Original";
			/*
			 * Original File can be either source code or plain text, so we must
			 * filter the two before performing detection
			 */
			File target = new File(targetDirectory);
			if (target.exists() && target.isDirectory()) {
				System.out.println("The target exists");
			}
			else {
				target.mkdir();
			}
			DirectoryProcessor text = new DirectoryProcessor(originalDirectory, new PlainTextFilter());
			File[] plainTextFiles = text.getInputFiles();

			DirectoryProcessor sourceCode = new DirectoryProcessor(originalDirectory, new JavaFileFilter());
			File[] sourceCodeFiles = sourceCode.getInputFiles();
			if (sourceCodeFiles.length == 0) {
				return null;
			}
			if (this.strategy == null) {
				System.out.println("strategy was not passed");
				return null;
			}
			edges = this.strategy.doDetection(sourceCodeFiles, userSettings.getOriginalProfile(), this.ngramlength);
			System.out.println(edges);
			return edges;
		}

		if (userSettings.getNoWSProfile().isInUse()) {
			System.out.println("No WS Detection");
			/* Get the files located in the NoWhitespace Directory */
			File noWSDirectory = new File(userSettings.getNoWSProfile().getOutputDir());
			System.out.println(noWSDirectory.getPath());
			File[] noWSFiles = noWSDirectory.listFiles();
		}

		if (userSettings.getNoCommentsProfile().isInUse()) {
			File noCommentsDirectory = new File(userSettings.getNoCommentsProfile().getOutputDir());
			System.out.println(noCommentsDirectory.getPath());
			File[] noCommentsFiles = noCommentsDirectory.listFiles();
			for (int i = 0; i < noCommentsFiles.length; i++) {
				System.out.println(noCommentsFiles[i].toString());
			}
		}

		if (userSettings.getNoCWSProfile().isInUse()) {
			System.out.println("No Comments, No WS Detection");
			File noCWSDirectory = new File(userSettings.getNoCWSProfile().getOutputDir());
			System.out.println(noCWSDirectory.getPath());
			File[] noCWSFiles = noCWSDirectory.listFiles();
		}

		if (userSettings.getCommentsProfile().isInUse()) {
			System.out.println("Comments Detection");
			File commentsDirectory = new File(userSettings.getCommentsProfile().getOutputDir());
			System.out.println(commentsDirectory.getPath());
			File[] commentsFiles = commentsDirectory.listFiles();
		}

		if (userSettings.getTokenisedProfile().isInUse()) {
			System.out.println("Token Detection");
			File tokDirectory = new File(userSettings.getTokenisedProfile().getOutputDir());
			System.out.println(tokDirectory.getPath());
			File[] tokFiles = tokDirectory.listFiles();
		}

		if (userSettings.getWSPatternProfile().isInUse()) {
			System.out.println("WS Pattern Detection");
			File wsPatternDirectory = new File(userSettings.getWSPatternProfile().getOutputDir());
			System.out.println(wsPatternDirectory.getPath());
			File[] wsPatternFiles = wsPatternDirectory.listFiles();
		}
		return null;
	}
}
