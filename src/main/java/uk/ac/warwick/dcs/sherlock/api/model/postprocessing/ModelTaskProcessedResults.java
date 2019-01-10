package uk.ac.warwick.dcs.sherlock.api.model.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.model.ISourceFile;

import java.util.*;

/**
 * Processed results for a task (for a single IDetector instance)
 *
 * Each file in this should be scored for its performance only in this task
 */
public class ModelTaskProcessedResults {

	private Map<ISourceFile, ResultFileData> fileMap;

	public ModelTaskProcessedResults() {
		this.fileMap = new HashMap<>();
	}

	/**
	 * Add a file to the map, return the empty ResultFileData instance created for the file
	 * @param file ISourceFile instance
	 * @return empty ResultFileData instance created for the file
	 */
	public ResultFileData addFile(ISourceFile file) {
		if (this.fileMap.containsKey(file)) {
			return this.fileMap.get(file);
		}

		ResultFileData d = new ResultFileData();
		this.fileMap.put(file, d);
		return d;
	}

	/**
	 * Get the ResultFileData instance for the file
	 * @param file ISourceFile instance
	 * @return ResultFileData instance
	 */
	public ResultFileData getFileData(ISourceFile file) {
		if (this.fileMap.containsKey(file)) {
			return this.fileMap.get(file);
		}
		return null;
	}

	class ResultFileData {

		// Overall score achieved by the file in this task
		private float overallFileScore;

		// Score achieved against each other file
		private Map<ISourceFile, Float> individualFileScores;



	}

	//class ResultFileBlockData

//THIS WILL CHANGE A LOT AGAIN, DONT USE YET
//
////	private ISourceFile file;
////	private DetectionType type;
////	private float overallScore;
////
////	private List<ResultsBlock> blockMap;
////	private Map<ISourceFile, ResultsFile> fileMap;
//
//	ISourceFile getFile();
//	DetectionType getType();
//	float getOverallScore();
//
//	Map<ISourceFile, IResultsFile> getFileMap();
//	List<IResultsBlock> getBlockList();
//
//	interface IResultsFile {
//
////		private float fileScore;
////		private List<IResultsBlock> blocksContaingFile;
//
//		float getFileScore();
//
//		List<IResultsBlock> getBlockList();
//	}
//
//	interface IResultsBlock {
//
////		Tuple<Integer, Integer> parentBlock;
////		float blockScore;
////		Map<ISourceFile, Tuple<Integer, Integer>> associatedBlocks;
//
//		Tuple<Integer, Integer> getParentBlock();
//		float getBlockScore();
//		Map<ISourceFile, Tuple<Integer, Integer>> getChildBlocks();
//	}
//
}
