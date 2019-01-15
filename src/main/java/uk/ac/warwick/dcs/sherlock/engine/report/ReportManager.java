package uk.ac.warwick.dcs.sherlock.engine.report;

import java.util.*;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.engine.report.FileReport;

/**
 * A class to handle report generation in general (does not generate reports itself).
 *
 * It takes all possible inputs that may be relevant from postprocessing, and handles requests for reports; sending
 * the relevant information to the actual report generator in use.
 *
 * very wip
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

	private AbstractReportGenerator reportGenerator;

	/**
	 *
	 * @param reportGenerator The implementation of AbstractReportGenerator that will generate all reports for this project.
	 */
	public ReportManager(AbstractReportGenerator reportGenerator) {
		this.reportGenerator = reportGenerator;

		reports = new HashMap<Long, FileReport>();
		fileIds = new ArrayList<Long>();
		codeBlockGroups = new ArrayList<ICodeBlockGroup>();
		variableNames = new HashMap<Long, List<String>>();
		methodNames = new HashMap<Long, List<String>>();
	}

	//TODO
	public void GenerateReports() {

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
