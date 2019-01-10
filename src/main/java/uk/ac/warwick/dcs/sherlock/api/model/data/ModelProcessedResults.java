package uk.ac.warwick.dcs.sherlock.api.model.data;

import java.util.*;

// Make interface and make a creator interface in the API
public class ModelProcessedResults {

	private Map<ISourceFile, ResultFileData> fileMap;

	class ResultFileData {

	}

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
