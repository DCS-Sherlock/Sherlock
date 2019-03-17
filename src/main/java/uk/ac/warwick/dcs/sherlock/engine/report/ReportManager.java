package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;

import java.util.*;

/**
 * A class to handle report generation in general (does not generate fileReportMap itself).
 * <p>
 * It takes all possible inputs that may be relevant from postprocessing, and handles requests for fileReportMap; sending the relevant information to the actual report generator in use.
 */
public class ReportManager {

	/**
	 * A list of every files' respective persistentIds. Mainly used for iteration purposes.
	 */
	private List<Long> fileIds;

	/**
	 * A map of all the individual files according to their persitent ids.
	 */
	private Map<Long, ISourceFile> fileMap;

	/**
	 * Contains most necessary information to generate fileReportMap for every file, including DetectionType, score, line numbers, etc.
	 */
	private List<ICodeBlockGroup> codeBlockGroups;

	/**
	 * Maps submission ids to a list of file ids contained by that submission.
	 */
	private Map<Long, List<Long>> submissionFileMap;

	/**
	 * All generated file reports are stored as FileReports. The key is each file's unique id.
	 */
	private Map<Long, FileReport> fileReportMap;

	/**
	 * A map of reports for an entire submission. The key is the submission's unique id, and the list of submission matches make up the report.
	 */
	private Map<Long, List<SubmissionMatch>> submissionReportMap;

	/**
	 * The object used to generate the report information.
	 */
	private IReportGenerator reportGenerator;

	/**
	 * Initialises the report manager (default).
	 */
	public ReportManager() {
		this.reportGenerator = new ReportGenerator();

		this.submissionFileMap = new HashMap<>();
		this.fileMap = new HashMap<>();
		this.fileReportMap = new HashMap<>();
		this.fileIds = new ArrayList<>();
		this.codeBlockGroups = new ArrayList<>();
		this.submissionReportMap = new HashMap<>();
	}

	/**
	 * Initialises the report manager and adds files and code block groups to it immediately.
	 * @param files The files to generate fileReportMap for (see AddFiles()).
	 * @param codeBlockGroups The matches that will be used by the Report Generator (see AddCodeBlockGroups()).
	 */
	public ReportManager(List<ISourceFile> files, List<ICodeBlockGroup> codeBlockGroups) {
		this.reportGenerator = new ReportGenerator();

		this.submissionFileMap = new HashMap<>();
		this.fileMap = new HashMap<>();
		this.fileReportMap = new HashMap<>();
		this.fileIds = new ArrayList<>();
		this.codeBlockGroups = new ArrayList<>();
		this.submissionReportMap = new HashMap<>();

		AddFiles(files);
		AddCodeBlockGroups(codeBlockGroups);
	}

	/**
	 * Initialises the report manager (use if different varieties of IReportGenerator are added).
	 * @param reportGenerator The implementation of IReportGenerator that will generate all fileReportMap for this project.
	 */
	public ReportManager(IReportGenerator reportGenerator) {
		this.reportGenerator = reportGenerator;

		this.submissionFileMap = new HashMap<>();
		this.fileMap = new HashMap<>();
		this.fileReportMap = new HashMap<>();
		this.fileIds = new ArrayList<>();
		this.codeBlockGroups = new ArrayList<>();
		this.submissionReportMap = new HashMap<>();
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
	 * To be called by the Post-Processor. Adds files info to fileIds, fileMap, and submissionFileMap.
	 *
	 * @param files A list of the ISourceFiles that have been run through the detector.
	 */
	public void AddFiles(List<ISourceFile> files) {
		for(ISourceFile file : files) {
			fileIds.add(file.getPersistentId());
			fileMap.put(file.getPersistentId(), file);

			//If this is the first file of this submission seen, create a new list and put it into submissionFileMap.
			//otherwise add to the existing list.
			if(submissionFileMap.get(file.getSubmissionId()) == null) {
				ArrayList<Long> idList = new ArrayList<>();
				idList.add(file.getPersistentId());
				submissionFileMap.put(file.getSubmissionId(), idList);
			} else {
				submissionFileMap.get(file.getSubmissionId()).add(file.getPersistentId());
			}
		}
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

		FileReport fileReport = reportGenerator.GenerateReport(sourceFile, relevantGroups);

		fileReportMap.put(sourceFile.getPersistentId(), fileReport);
		return fileReport;
	}

	/**
	 * Generates a report for all ISourceFiles in fileMap that don't have a report already generated for them.
	 */
	public void GenerateAllReports() {
		for(Long fileId : fileIds) {
			if(!fileReportMap.containsKey(fileId))
				GenerateReport(fileMap.get(fileId));
		}
	}

	/**
	 * To be called by the web report pages. Gets a list of submission summaries
	 * @return a list of the matching SubmissionSummaries, each containing their ids, overall scores, and a list of the submissions that they were matched with.
	 */
	public List<SubmissionSummary> GetMatchingSubmissions() {
		ArrayList<SubmissionSummary> output = new ArrayList<>();

		for(Long submissionId : submissionFileMap.keySet()) {
			//TODO: need to get the proper overall score somehow
			Random tempRandom = new Random();
			float overallScore = tempRandom.nextFloat();
			SubmissionSummary submissionSummary = new SubmissionSummary(submissionId, overallScore);
			ArrayList<Tuple<Long, Float>> matchingSubs = new ArrayList<>();

			//Look through all the code block groups to determine which submissions have been matched with which other submissions.
			for(ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
				//If this group contains a file for the current submission, add all other submissions in the group to the list.
				if(codeBlockGroup.submissionIdPresent(submissionId)) {
					for(ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
						if(codeBlock.getFile().getSubmissionId() != submissionId) {
							//TODO: need to add the proper relative score somehow
							float relativeScore = tempRandom.nextFloat();
							matchingSubs.add(new Tuple<>(codeBlock.getFile().getSubmissionId(), relativeScore));
						}
					}
				}

			}
			submissionSummary.AddMatchingSubmissions(matchingSubs);
			output.add(submissionSummary);
		}

		return output;
	}

	/**
	 * Compares two submissions, finds all the matches in files they contain between them, and returns all relevant information about them.
	 *
	 * @param submissions The submissions to compare (should be a list of two submissions only; any submissions beyond the first two are ignored)
	 * @return A list of SubmissionMatch objects which contain ids of the two matching files, a score for the match, a reason from the DetectionType, and the line numbers in each file where the match occurs.
	 */
	public List<SubmissionMatch> GetSubmissionComparison(List<ISubmission> submissions) {
		List<ICodeBlockGroup> relevantGroups = new ArrayList<>();

		if(submissions.size() < 2)
			return null;

		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			if(codeBlockGroup.submissionIdPresent(submissions.get(0).getId()) &&
			codeBlockGroup.submissionIdPresent(submissions.get(1).getId()))
				relevantGroups.add(codeBlockGroup);
		}

		return reportGenerator.GenerateSubmissionComparison(submissions, relevantGroups);
	}

	/**
	 * Generate a report for a single submission, containing all matches for all files within it.
	 *
	 * @param submission The submission to generate the report for.
	 * @return A list of SubmissionMatch objects which contain ids of the two matching files, a score for the match, a reason from the DetectionType, and the line numbers in each file where the match occurs.
	 */
	public List<SubmissionMatch> GetSubmissionReport(ISubmission submission) {

		List<ICodeBlockGroup> relevantGroups = new ArrayList<>();

		for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
			if(codeBlockGroup.submissionIdPresent(submission.getId()))
				relevantGroups.add(codeBlockGroup);
		}

		//Store this report and return it.
		List<SubmissionMatch> submissionReport = reportGenerator.GenerateSubmissionReport(submission, relevantGroups);
		submissionReportMap.put(submission.getId(), submissionReport);
		return submissionReport;
	}

	/**
	 * Retrieve an already-generated submission report for the specified submission id.
	 * @param submissionId the id of the submission to get the report for.
	 * @return The list of submission matches making up the report if it exists; an empty list if there is no such report.
	 */
	public List<SubmissionMatch> GetSubmissionReport(long submissionId) {
		//If the report for this submission already exists, return it
		if (submissionReportMap.containsKey(submissionId))
			return submissionReportMap.get(submissionId);
		else
			return new ArrayList<SubmissionMatch>();
	}

	/**
	 * Retrieve a file report which has already been generated.
	 * TODO: print something to error if exception? idk
	 *
	 * @param sourceFile The file to retrieve the report for
	 * @return a FileReport object that is the report in question
	 * @throws NullPointerException if there is no report for this file
	 */
	public FileReport GetFileReport(ISourceFile sourceFile) throws NullPointerException {
		try {
			return fileReportMap.get(sourceFile.getPersistentId());
		} catch(NullPointerException e) {
			throw e;
		}
	}

}
