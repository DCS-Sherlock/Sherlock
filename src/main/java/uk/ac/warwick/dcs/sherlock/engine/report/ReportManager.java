package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import javax.validation.constraints.Null;
import java.util.*;

/**
 * A class to handle report generation in general (does not generate reports itself).
 * <p>
 * It takes all possible inputs that may be relevant from postprocessing, and handles requests for reports; sending the relevant information to the actual report generator in use.
 */
public class ReportManager {

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
	 * <p>
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
	 * @param fileIds A list of very file's unique, persistent id.
	 */
	public void AddFileIds(List<Long> fileIds) {
		this.fileIds.addAll(fileIds);
	}

	/**
	 * To be called by the PostProcessor.
	 *
	 * @param methodNames Each list contains every method name for each file.
	 */
	public void AddMethodNames(Map<Long, List<String>> methodNames) {
		this.methodNames.putAll(methodNames);
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
	 * Generates a report for a single specified file, stores it, and returns it.
	 *
	 * @param sourceFile The file to generate a report for.
	 *
	 * @return The FileReport object that is generated.
	 */
	public FileReport GenerateReport(ISourceFile sourceFile) {
		List<ICodeBlockGroup> relevantGroups = new ArrayList<>();

		//Get all the codeblockgroups which contain the desired file.
		//this is kind of gross honestly with the current setup
		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			List<? extends ICodeBlock> codeBlocks = codeBlockGroup.getCodeBlocks();

			boolean fileInGroup = false;
			for (ICodeBlock codeBlock : codeBlocks) {
				if (codeBlock.getFile().getPersistentId() == sourceFile.getPersistentId()) {
					fileInGroup = true;
					break;
				}
			}

			relevantGroups.add(codeBlockGroup);
		}

		FileReport fileReport = reportGenerator.GenerateReport(sourceFile, relevantGroups, variableNames.get(sourceFile.getPersistentId()));

		reports.put(sourceFile.getPersistentId(), fileReport);
		return fileReport;
	}

	/**
	 * Retrieve a report which has already been generated.
	 * TODO: print something to error if exception? idk
	 *
	 * @param sourceFile The file to retrieve the report for
	 * @return a FileReport object that is the report in question
	 * @throws NullPointerException if there is no report for this file
	 */
	public FileReport GetReport(ISourceFile sourceFile) throws NullPointerException {
		try {
			return reports.get(sourceFile.getPersistentId());
		} catch(NullPointerException e) {
			throw e;
		}
	}

}
