package uk.ac.warwick.dcs.sherlock.engine.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultJob;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultTask;

import java.util.*;

/**
 * A class to handle report generation in general (does not generate fileReportMap itself).
 * <p>
 * It takes all possible inputs that may be relevant from postprocessing, and handles requests for fileReportMap; sending the relevant information to the actual report generator in use.
 */
public class ReportManager {

	/**
	 * Logger instance
	 */
	private final Logger logger = LoggerFactory.getLogger(ReportManager.class);

	/**
	 * Contains all the information needed to generate reports from.
	 */
	private IResultJob results;

	/**
	 * A map of all the individual files according to their persitent ids.
	 */
	private Map<Long, ISourceFile> fileMap;

	/**
	 * Maps submission ids to a list of file ids contained by that submission.
	 */
	private Map<Long, List<Long>> submissionFileMap;

	/**
	 * A map of reports for an entire submission. The key is the submission's unique id, and the list of submission match groups make up the report.
	 */
	private Map<Long, List<SubmissionMatchGroup>> submissionReportMap;


	/**
	 * Maps submission ids to their overall scores
	 */
	private Map<Long, Float> submissionScores;

	/**
	 * Maps pairs of file ids to the relative scores between them
	 */
	private Map<ITuple<Long, Long>, Float> relativeFileScores;

	/**
	 * The object used to generate the report information.
	 */
	private IReportGenerator reportGenerator;

	/**
	 * Initialises the report manager with a set of results.
	 * @param results Contains a full set of results from postprocessing; all useful info is extracted from here
	 */
	public ReportManager(IResultJob results) {
		this.reportGenerator = new ReportGenerator();

		this.submissionFileMap = new HashMap<>();
		this.fileMap = new HashMap<>();
		this.submissionReportMap = new HashMap<>();
		this.results = results;
		this.submissionScores = new HashMap<>();
		this.relativeFileScores = new HashMap<>();
		this.results.getFileResults().forEach(resultFile -> this.fileMap.put(resultFile.getFile().getPersistentId(), resultFile.getFile()));
		this.results.getFileResults().forEach(resultFile -> resultFile.getFileScores().keySet().forEach(file2 -> this.relativeFileScores.put(new Tuple<Long, Long>(resultFile.getFile().getPersistentId(), file2.getPersistentId()), resultFile.getFileScore(file2))));
		this.results.getFileResults().forEach(resultFile -> this.submissionScores.put(resultFile.getFile().getSubmission().getId(), resultFile.getOverallScore()));

		FillSubmissionFileMap();
	}

	private List<IResultTask> GetResultTasks(ISubmission submission) {
		List<IResultTask> resultTasks = new ArrayList<>();
		this.results.getFileResults().stream().flatMap(file -> file.getTaskResults().stream()).filter(task -> task.getContainingBlocks() != null)
				.filter(task -> task.getContainingBlocks().stream().anyMatch(group -> group.submissionIdPresent(submission.getId()))).forEach(task -> resultTasks.add(task));
		return resultTasks;
	}

	private List<IResultTask> GetResultTasks(ITuple<ISubmission, ISubmission> submissions) {
		List<IResultTask> resultTasks = new ArrayList<>();
		this.results.getFileResults().stream().flatMap(file -> file.getTaskResults().stream()).filter(task -> task.getContainingBlocks() != null)
				.filter(task -> task.getContainingBlocks().stream().anyMatch(group -> group.submissionIdPresent(submissions.getKey().getId()) || group.submissionIdPresent(submissions.getValue().getId()))).forEach(task -> resultTasks.add(task));
		return resultTasks;
	}

	private List<ICodeBlockGroup> GetCodeBlockGroups() {
		List<ICodeBlockGroup> codeBlockGroups = new ArrayList<>();
		this.results.getFileResults().stream().flatMap(file -> file.getTaskResults().stream()).filter(task -> task.getContainingBlocks() != null)
				.forEach(task -> codeBlockGroups.addAll(task.getContainingBlocks()));
		return codeBlockGroups;
	}

	private List<ISourceFile> GetSourceFiles() {
		List<ISourceFile> sourceFiles = new ArrayList<>();
		this.results.getFileResults().forEach(file -> sourceFiles.add(file.getFile()));
		return sourceFiles;
	}

	/**
	 * Fill in submissionFileMap based off of the contents of fileMap.
	 */
	private void FillSubmissionFileMap() {
		for(ISourceFile file : this.fileMap.values()) {
			//If this is the first file of this submission seen, create a new list and put it into submissionFileMap.
			//otherwise add to the existing list.
			if(submissionFileMap.get(file.getSubmission().getId()) == null) {
				ArrayList<Long> idList = new ArrayList<>();
				idList.add(file.getPersistentId());
				submissionFileMap.put(file.getSubmission().getId(), idList);
			} else {
				submissionFileMap.get(file.getSubmission().getId()).add(file.getPersistentId());
			}
		}
	}

	/**
	 * To be called by the web report pages. Gets a list of submission summaries.
	 * @return a list of the matching SubmissionSummaries, each containing their ids, overall scores, and a list of the submissions that they were matched with.
	 */
	public List<SubmissionSummary> GetMatchingSubmissions() {
		ArrayList<SubmissionSummary> output = new ArrayList<>();

		for(Long submissionId : submissionFileMap.keySet()) {
			Random tempRandom = new Random();
			float overallScore = submissionScores.get(submissionId);
			SubmissionSummary submissionSummary = new SubmissionSummary(submissionId, overallScore);
			ArrayList<Tuple<Long, Float>> matchingSubs = new ArrayList<>();
			ArrayList<Long> matchingSubIds = new ArrayList<>();

			//Look through all the code block groups to determine which submissions have been matched with which other submissions.
			for(ICodeBlockGroup codeBlockGroup : GetCodeBlockGroups()) {
				//If this group contains a file for the current submission, add all other submissions in the group to the list, if they aren't already added
				if(codeBlockGroup.submissionIdPresent(submissionId)) {
					for(ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
						long currentId = codeBlock.getFile().getSubmission().getId();
						if(currentId != submissionId && !matchingSubIds.contains(currentId)) {
							//TODO: need to add the proper relative score somehow
							float relativeScore = tempRandom.nextFloat();
							//relativeScore = results.getFileResults().stream().
							matchingSubs.add(new Tuple<>(currentId, relativeScore));
							matchingSubIds.add(currentId);
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
	 * @return A list of SubmissionMatchGroup objects which contain lists of SubmissionMatch objects; each have ids of the two matching files, a score for the match, a reason from the DetectionType, and the line numbers in each file where the match occurs.
	 */
	public List<SubmissionMatchGroup> GetSubmissionComparison(List<ISubmission> submissions) {
		if (submissions.stream().anyMatch(ISubmission::hasParent)) {
			// one of the submissions is not top level, don't make reports on non-top level submission.

			//Either just return null with an error msg
			logger.error("Cannot generate comparison on a submission which has a parent");
			return null;

			// or get the parent recursively and generate on that!
			/*ISubmission sub = ...
			while (sub.hasParent()) {
				sub = sub.getParent();
			}*/
		}

		if(submissions.size() < 2)
			return null;

		List<IResultTask> relevantTasks = GetResultTasks(new Tuple<>(submissions.get(0), submissions.get(1)));

		return reportGenerator.GenerateSubmissionComparison(submissions, relevantTasks);
	}

	/**
	 * Generate a report for a single submission, containing all matches for all files within it, and a summary of the report as a string.
	 *
	 * @param submission The submission to generate the report for.
	 * @return A tuple. The key contains a list of SubmissionMatchGroup objects which contain lists of SubmissionMatch objects; each have objects which contain ids of the two matching files, a score for the match, a reason from the DetectionType, and the line numbers in each file where the match occurs. The value is the report summary.
	 */
	public ITuple<List<SubmissionMatchGroup>, String> GetSubmissionReport(ISubmission submission) {

		if (submission.hasParent()) {
			// submission is not top level, don't make reports on non-top level submission.

			//Either just return null with an error msg
			logger.error("Cannot generate report on a submission which has a parent");
			return null;

			// or get the parent recursively
			/*ISubmission sub = ...
			while (sub.hasParent()) {
				sub = sub.getParent();
			}*/
		}

		List<IResultTask> relevantTasks = GetResultTasks(submission);

		//Store this report and return it.
		ITuple<List<SubmissionMatchGroup>, String> submissionReport = reportGenerator.GenerateSubmissionReport(submission, relevantTasks, submissionScores.get(submission.getId()));
		submissionReportMap.put(submission.getId(), submissionReport.getKey());
		return submissionReport;
	}

	/**
	 * Retrieve an already-generated submission report for the specified submission id.
	 * @param submissionId the id of the submission to get the report for.
	 * @return The list of submission matches making up the report if it exists; an empty list if there is no such report.
	 */
	public List<SubmissionMatchGroup> GetSubmissionReport(long submissionId) {
		//If the report for this submission already exists, return it
		if (submissionReportMap.containsKey(submissionId))
			return submissionReportMap.get(submissionId);
		else
			return new ArrayList<>();
	}

}
