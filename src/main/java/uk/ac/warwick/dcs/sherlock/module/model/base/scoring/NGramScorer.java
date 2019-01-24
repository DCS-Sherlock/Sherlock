package uk.ac.warwick.dcs.sherlock.module.model.base.scoring;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.scoring.IScoreFunction;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NgramMatch;

import java.util.*;

/**
 * TODO: WRITE THIS
 *
 * Store the num objects (from rawresult object) for each of the files,
 * compare the number of non-overlapping groups file is present in with the total number of objects to get a score for the file
 */
public class NGramScorer implements IScoreFunction {

	private float threshold;

	public ArrayList<ISourceFile> file_list;		// public to allow use in external loops
	private ArrayList<FileInfo> file_info;

	public NGramScorer(float threshold) {
		this.threshold = threshold;
	}

	@Override
	public float score(ISourceFile mainFile, ISourceFile referenceFile, List<ICodeBlockGroup> mutualGroups) {
		return 0;
	}

	public void newGroup() {
		file_list = new ArrayList<>();
		file_info = new ArrayList<>();
	}

	public void add(NgramMatch pair) {
		// check for if the files exist in file_list, if they do add to them, if not make a new object to add to the list
		if (file_list.contains(pair.file1)) {
			// acquire the respective file_info index and update it with the new similarity score
			file_info.get(file_list.indexOf(pair.file1)).addToFileInfo(pair.similarity);
		} else {
			// add the new file and a respective FileInfo object (ass they are always added in pairs the indexes will always match)
			file_list.add(pair.file1);
			file_info.add(new FileInfo(pair.similarity, pair.reference_lines));
		}
		// duplicate of above for the second file in the pair. TODO: find a way to merge these dupicate code blocks (may require NgramMatch changes)
		if (file_list.contains(pair.file2)) {
			// acquire the respective file_info index and update it with the new similarity score
			file_info.get(file_list.indexOf(pair.file2)).addToFileInfo(pair.similarity);
		} else {
			// add the new file and a respective FileInfo object (ass they are always added in pairs the indexes will always match)
			file_list.add(pair.file2);
			file_info.add(new FileInfo(pair.similarity, pair.check_lines));
		}
	}

	public boolean checkSize(int file_count) {
		return (file_list.size() / file_count) <= threshold;
	}

	public float getScore(ISourceFile file, ICodeBlockGroup out_group) {
		// calculate a suitable score for the inputted file based on the available data
		int index = file_list.indexOf(file);
		// placeholder score, currently produces an index weighted by rarity and general match strength
		float score = file_info.get(index).total_similarity / file_list.size();

		out_group.addCodeBlock(file, score, file_info.get(index).lines);
		return 0;
	}

	class FileInfo {

		public float total_similarity;
		public int similar_files;
		public Tuple<Integer, Integer> lines;

		public FileInfo(float similarity, Tuple<Integer, Integer> lines) {
			total_similarity = similarity;
			similar_files = 1;
			this.lines = lines;
		}

		public void addToFileInfo(float similarity) {
			total_similarity += similarity;
			similar_files++;
		}
	}
}


