package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.report.FileReport;

import java.util.*;

/**
 * A class to handle report generation in general (does not generate reports itself).
 *
 * It takes all possible inputs that may be relevant from postprocessing, and handles requests for reports; sending
 * the relevant information to the actual report generator in use.
 */

/**
 * Notes on generating a report:
 *
 * FileReport basically gets a list of strings
 * Have some collection of codeblockgroups
 * to make a report, take all codeblockgroups from that collection that have stuff from the file in question
 * give those codeblockgroups to a report generator, which goes through each codeblockgroup and generates a string(s) for it, and puts it in the FileReport
 * Reorder the list in FileReport if necessary/desired
 * add anything else to the FileReport if needed
 * done
 */

public class ReportManager {
	//Info to be stored one way or another. Not sure about format yet.
	//
	//files
	//line numbers (either a range or list)
	//column/where in line numbers (where necessary)
	//detection type
	//percentage/score/whatever
	//Variable names
	//Method names

	/**
	 * A list of every files' respective persistentIds. Mainly used for iteration purposes.
	 */
	private List<Long> fileIds;

	/**
	 * Contains most necessary information to generate reports for every file, including DetectionType, score, line numbers, etc.
	 */
	private List<ICodeBlockGroup> codeBlockGroups;

	/**
	 * These two contain, for every file being examined (based on their unique persistentId), a list of their variable and method names respectively.
	 *
	 * TODO: may be a better way of combining this with codeBlockGroups?
	 */
	private Map<Long, List<String>> variableNames;
	private Map<Long, List<String>> methodNames;

	/**
	 * All generated reports are stored as FileReports. The key is each file's unique id.
	 */
	private Map<Long, FileReport> reports;

	private IReportGenerator reportGenerator;

	/**
	 *
	 * @param reportGenerator The implementation of IReportGenerator that will generate all reports for this project.
	 */
	public ReportManager(IReportGenerator reportGenerator) {
		this.reportGenerator = reportGenerator;

		reports = new HashMap<>();
		fileIds = new ArrayList<>();
		codeBlockGroups = new ArrayList<>();
		variableNames = new HashMap<>();
		methodNames = new HashMap<>();
	}


	/**
	 * Generates a report for a single specified file, stores it, and returns it.
	 * @param fileId The persistent ID of the file to generate a report for.
	 * @return The FileReport object that is generated.
	 */
	public FileReport GenerateReport(long fileId) {
		List<ICodeBlockGroup> relevantGroups = new ArrayList<>();

		//Get all the codeblockgroups which contain the desired file.
		//this is kind of gross honestly with the current setup
		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			List<? extends ICodeBlock> codeBlocks = codeBlockGroup.getCodeBlocks();

			boolean fileInGroup = false;
			for (ICodeBlock codeBlock : codeBlocks) {
				if (codeBlock.getFile().getPersistentId() == fileId) {
					fileInGroup = true;
					break;
				}
			}

			relevantGroups.add(codeBlockGroup);
		}

		FileReport fileReport = reportGenerator.GenerateReport(fileId, relevantGroups);

		reports.put(fileId, fileReport);
		return fileReport;
	}

	/**
	 * To be called by the Post-Processor.
	 *
	 * @param fileIds A list of very file's unique, persistent id.
	 */
	public void AddFileIds(List<Long> fileIds) {
		this.fileIds.addAll(fileIds);
	}

	/**
	 * To be called by the Post-Processor.
	 *
	 * @param codeBlockGroups All ICodeBlockGroups with relevant information for the Report Generator to work with.
	 */
	public void AddCodeBlockGroups(List<ICodeBlockGroup> codeBlockGroups) {
		this.codeBlockGroups.addAll(codeBlockGroups);
	}

	/**
	 * To be called by the Post-Processor.
	 *
	 * @param variableNames Each list contains every variable name for each file.
	 */
	public void AddVariableNames(Map<Long, List<String>> variableNames) {
		this.variableNames.putAll(variableNames);
	}

	/**
	 * To be called by the PostProcessor.
	 *
	 * @param methodNames Each list contains every method name for each file.
	 */
	public void AddMethodNames(Map<Long, List<String>> methodNames) {
		this.methodNames.putAll(methodNames);
	}

}
