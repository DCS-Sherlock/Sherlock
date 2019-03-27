package uk.ac.warwick.dcs.sherlock.module.model.base.scoring;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NgramMatch;

import java.util.*;

/**
 *
 */
public class NGramScorer {

	private float threshold;

	// the list of files in the current match group
	public ArrayList<ISourceFile> file_list;		// public to allow use in external loops
	// The scoring info for each file
	private ArrayList<FileInfo> file_info;

	// List to store file pairs by file index
	// the list of matched pair object groups (depreciated, was used in general score function)
	private ArrayList<MatchList> file_matches;
	// the list of each file the above groups should be attached to (likewise depreciated)
	private ArrayList<ISourceFile> match_list;

	/**
	 * Object constructor.
	 * @param threshold The threshold used by the postprocessor to determine if cases are common.
	 */
	public NGramScorer(float threshold) {
		this.threshold = threshold;
		file_matches = new ArrayList<>();
		match_list = new ArrayList<>();
	}

	/**
	 * Returns the total similarity score between the 2 passed files
	 * <p>
	 *     WARNING: This currently returns a total of ALL matches between the two files, it does not ignore those
	 *     considered common and removed from final scoring groups. In future this issue will be resolved
	 * </p>
	 * @param mainFile      the subject of the scoring function.
	 * @param referenceFile the reference to score the subject against
	 * @param mutualGroups  the ICodeBlockGroups where both the main and subject files are present
	 *
	 * @return Total similarity score for all relevant file match pairs
	 */
	@Deprecated
	public float score(ISourceFile mainFile, ISourceFile referenceFile, List<ICodeBlockGroup> mutualGroups) {
		// counter for total score
		float accumulator = 0.0f;
		// instance multiplier for each match based on it's line size
		int line_multiplier = 1;
		// check if first file is in list
		int index = match_list.lastIndexOf(mainFile);
		// if not in list then is paired with something in the list so check second file
		if (index == -1) {
			index = match_list.lastIndexOf(referenceFile);
			// if not in the list print debug message
			// DEPTODO: this can be exception handling
			if (index == -1) {
				System.out.println("File pair: (" + mainFile.getFileDisplayName() + ", " + referenceFile.getFileDisplayName() + ") cannot be found in list of files:");
				for (ISourceFile file : match_list ) {
					System.out.println(file.getFileDisplayName());
				}
			}
			// where the match is between the 2 files passed increment the accumulator
			for (NgramMatch match : file_matches.get(index).matches) {
				if (match.files[0].equals(mainFile) && !match.common) {
					line_multiplier = match.lines.get(1).getKey() - match.lines.get(1).getValue();
					accumulator += match.similarity * line_multiplier;
				}
			}
		} else {
			// where the match is between the 2 files passed increment the accumulator
			for (NgramMatch match : file_matches.get(index).matches) {
				if (match.files[1].equals(referenceFile) && !match.common) {
					line_multiplier = match.lines.get(1).getKey() - match.lines.get(1).getValue();
					accumulator += match.similarity * line_multiplier;
				}
			}
		}
		// DEPTODO: resolve issues with overlapping blocks counting as duplicate lines for the sake of the line count
		// normalise the score by the size of each file
		accumulator = (accumulator / mainFile.getTotalLineCount()) + (accumulator / referenceFile.getTotalLineCount());
		// return cumulative score normalised and averaged over the 2 files
		System.out.println(accumulator);
		return accumulator / 2;
	}

	/**
	 * Resets/Initialises the group info storage for each match group.
	 */
	public void newGroup() {
		file_list = new ArrayList<>();
		file_info = new ArrayList<>();
	}

	/**
	 * Adds files and their score info to the group data structure.
	 * <p>
	 *     Builds a list of all files (file_list) and adds each pair that is in that file
	 * </p>
	 * @param pair The pair of files and their local match score.
	 */
	public void add(NgramMatch pair) {
		// for both files
		for (int i = 0 ; i < 2 ; i++) {
			// check for if the files exist in file_list, if they do add to them, if not make a new object to add to the list
			if (file_list.contains(pair.files[i])) {
				// acquire the respective file_info index and update it with the new similarity score
				file_info.get(file_list.indexOf(pair.files[i])).addToFileInfo(pair.similarity);
				// add the pair to the match lists
//				file_matches.get(match_list.indexOf(file)).matches.add(pair);
			} else {
				// add the new file and a respective FileInfo object (ass they are always added in pairs the indexes will always match)
				file_list.add(pair.files[i]);
				file_info.add(new FileInfo(pair.similarity, pair.lines.get(i)));
				// create a new match list and add the pair to it
//				match_list.add(file);
//				file_matches.add(new MatchList(pair));
			}
		}
	}

	/**
	 * Check if the set of files is enough to be considered "common".
	 * @param file_count The number of files the system is comparing.
	 * @return A boolean saying if the file group should be ignored or not.
	 */
	public boolean checkSize(int file_count, ArrayList<NgramMatch> list) {
		// if the match is uncommon return true
		if ((file_list.size() / file_count) <= threshold) {
			return true;
		}
		// if the match is common set all pairs in the list to be common
		// this prevents common matches from being added to the overall file score
		else {
			for (NgramMatch match : list) {
				match.common = true;
			}
			// and then return false
			return false;
		}
	}

	/**
	 * Adds a block for the current file to the current groups output data structure along with its score.
	 * @param file The file that's block is being referenced.
	 * @param out_group The current block groups output structure.
	 */
	public void addScoredBlock(ISourceFile file, ICodeBlockGroup out_group) {
		// calculate a suitable score for the inputted file based on the available data
		int index = file_list.indexOf(file);
		// placeholder score, currently produces an index weighted by rarity and general match strength
		float score = file_info.get(index).total_similarity / file_list.size();

		out_group.addCodeBlock(file, score, file_info.get(index).lines);
		return;
	}

	/**
	 * Object used to store cumulative similarity score for a file in a match block.
	 */
	class FileInfo {

		/**
		 * The total of all similarity scores involving this file.
		 */
		public float total_similarity;
		/**
		 * The number of files that have been matched to this one for this code block.
		 */
		public int similar_files;
		/**
		 * The position of the block being referenced.
		 */
		public Tuple<Integer, Integer> lines;

		/**
		 * Constructor, adds first match info set and the relevant line position.
		 * @param similarity The similarity score of the first matched pair.
		 * @param lines The line positions of the referenced section.
		 */
		public FileInfo(float similarity, Tuple<Integer, Integer> lines) {
			total_similarity = similarity;
			similar_files = 1;
			this.lines = lines;
		}

		/**
		 * Adds a new similarity score to the total and keeps count of how many times a score has been added.
		 * @param similarity the score to be added.
		 */
		public void addToFileInfo(float similarity) {
			total_similarity += similarity;
			similar_files++;
		}
	}

	/**
	 * Used to store the set of matches associated with a file for use in file wide score generation.
	 */
	@Deprecated
	class MatchList {

		/**
		 * The list of matched pairs. Public to allow external additions.
		 */
		public ArrayList<NgramMatch> matches;

		/**
		 * Constructor, initialises storage and adds first element.
		 * @param pair
		 */
		public MatchList(NgramMatch pair) {
			matches = new ArrayList<>();
			matches.add(pair);
		}
	}
}
