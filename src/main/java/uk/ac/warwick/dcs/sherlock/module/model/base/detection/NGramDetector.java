package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.detection.PairwiseDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.PairwiseDetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NGramDetector.NGramDetectorWorker;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.NGramRawResult;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.TrimWhitespaceOnly;

import java.util.*;

public class NGramDetector extends PairwiseDetector<NGramDetectorWorker> {

	/**
	 * The character width of each N-Gram used in the detection.
	 * <p>
	 * In theory smaller is more sensitive, but realistically you don't want to use lower than 3 or higher than 8.
	 * </p>
	 */
	@AdjustableParameter (name = "N-Gram Size", defaultValue = 4, minimumBound = 1, maxumumBound = 10, step = 1)
	public int ngram_size;
	/**
	 * The minimum size of a list of N-Grams before checks begin.
	 * <p>
	 * N-Grams are put into a linked list when being matched, to prevent a match being detected for a short number of N-Grams (e.g. picking up things like a for loop) a minimum window size is used.
	 * Before this size is reached if the match ends then nothing is flagged.
	 * </p>
	 */
	@AdjustableParameter (name = "Minimum Window", defaultValue = 5, minimumBound = 0, maxumumBound = 20, step = 1)
	public int minimum_window;
	/**
	 * The threshold on the similarity value over which something is considered suspicious.
	 * <p>
	 * The 2 lists of N-Grams are compared to produce a similaity value between 0 and 1, with 1 being identical. This threshold decides at what point to consider a segment as similar, and when it's
	 * long enough to consider it possible plagerism.
	 * </p>
	 */
	@AdjustableParameter (name = "Threshold", defaultValue = 0.8f, minimumBound = 0.0f, maxumumBound = 1.0f, step = 0.001f)
	public float threshold;
	NGramRawResult<NgramMatch> res;

	public NGramDetector() {
		super("N-Gram Detector", "N-Gram implementation", NGramDetectorWorker.class, PreProcessingStrategy.of("no_whitespace", TrimWhitespaceOnly.class));
	}

	/**
	 * Compare 2 lists of N-grams and return a similarity metric
	 * <p>
	 * Finds the Jaccard Similarity of the 2 lists of Ngrams
	 * </p>
	 *
	 * @param string1 The reference N-gram list
	 * @param string2 The check N-gram list
	 *
	 * @return The float val for Jaccard Similarity
	 */
	public float compare(ArrayList<Ngram> string1, ArrayList<Ngram> string2) {
		// init the counters
		int same = 0;
		int dis1 = 0;
		int dis2 = 0;
		// build string lists for easier comparison
		ArrayList<String> string1List = new ArrayList<String>();
		ArrayList<String> string2List = new ArrayList<String>();

		// convert N-gram lists to String lists
		// this is done for direct comparisons of interior variables to be easier by using libraries
		for (Ngram ngram : string1) {
			string1List.add(ngram.getNgram());
		}
		for (Ngram ngram : string2) {
			string2List.add(ngram.getNgram());
		}

		// for each N-gram in the reference list count the number that match and that don't
		for (String ngram : string1List) {
			if (string2List.contains(ngram)) {
				same++;
			}
			else {
				dis1++;
			}
		}
		// for each N-gram in the check list count the number that don't match (matches counted in last loop)
		for (String ngram : string2List) {
			if (!string1List.contains(ngram)) {
				dis2++;
			}
		}

		// calculate and return the Jaccard Similarity
		return (float) same / ((float) same + (float) dis1 + (float) dis2);
	}

	// add line markers
	public void matchFound(ArrayList<Ngram> reference, ArrayList<Ngram> check, Ngram head, float last_peak, int since_last_peak, ISourceFile file1, ISourceFile file2) {
		// take out values back to the last peak
		for (int i = 0; i < since_last_peak; i++) {
			reference.remove(reference.size() - 1);
			check.remove(check.size() - 1);
		}
		// if the last peak is before the minimum window size skip the match construction (ignore case)
		if (reference.size() >= minimum_window) {
			// build an N-Gram match object to send to the post processor
			NgramMatch temp =
					new NgramMatch(reference.get(0).getLineNumber(), reference.get(reference.size() - 1).getLineNumber(), check.get(0).getLineNumber(), check.get(check.size() - 1).getLineNumber(),
							last_peak, file1, file2);
			// put an N-gram match into res along wih the start points of the segment in reference file then checked file.
			res.put(temp, reference.get(0).getLineNumber(), reference.get(reference.size() - 1).getLineNumber(), check.get(0).getLineNumber(), check.get(check.size() - 1).getLineNumber());
		}

		// empties the lists for next detection
		reference.clear();
		check.clear();
	}

	/**
	 * Load the contents of a file into a linked list of N-grams for easy reference
	 * <p>
	 * Each line of the file is taken and converted into N-grams which are in turn put into a linked list as N-gram objects containing the N-gram and it's line number
	 * </p>
	 *
	 * @param storage_list The list the N-grams are going to be stored in
	 * @param file         The list of lines in a file to be converted and stored
	 */
	private void loadNgramList(ArrayList<Ngram> storage_list, ArrayList<IndexedString> file) {
		// the N-gram string
		String substr;
		// the new N-gram object
		Ngram ngram = null;
		int line_number = 0;

		// variable to extract the string from the indexed container
		String line;
		// for each line get the indexed container
		for (IndexedString lineC : file) {
			// acquire line
			line = lineC.getValue();
			// if line is shorter than the ngram_size pad it with whitespace
			// this should function without issue as an equivalent lines will also be too short and be padded the same
			if (line.length() < ngram_size) {
				// pad to the size of an ngram
				for (int i = ngram_size - line.length(); i >= 0; i--) {
					line += " ";
				}
			}
			// acquire line number
			line_number = lineC.getKey();
			// for each N-gram in a line
			for (int i = 0; i < line.length() - (ngram_size - 1); i++) {
				// build an N-gram of ngram_size
				substr = line.substring(i, i + ngram_size);
				// create the next N-gram object with its line number
				ngram = new Ngram(substr, line_number);
				// add ngram to the list
				storage_list.add(ngram);
			}
		}
	}

	/**
	 * Load the contents of a file into an N-gram map for easy retrival
	 * <p>
	 * Each line of the file is taken in and converted into N-grams, then stored in a hash map as an object containing the N-gram, its line number, it's ID and the next N-gram in the file (modeled as
	 * a linked list).
	 * </p>
	 *
	 * @param storage_map The hashmap used to store the resulting N-grams
	 * @param file        The file data to be deconstucted into and stored as ordered N-grams
	 */
	private void loadNgramMap(HashMap<String, Ngram> storage_map, ArrayList<IndexedString> file) {
		// the N-gram string
		String substr;
		// the new N-gram object
		Ngram ngram = null;
		int line_number = 0;

		// variable to extract the string from the indexed container
		String line;
		// for each line get the indexed container
		for (IndexedString lineC : file) {
			// acquire line
			line = lineC.getValue();
			// if line is shorter than the ngram_size pad it with whitespace
			// this should function without issue as an equivalent lines will also be too short and be padded the same
			if (line.length() < ngram_size) {
				// pad to the size of an ngram
				for (int i = ngram_size - line.length(); i >= 0; i--) {
					line += " ";
				}
			}
			// acquire line number
			line_number = lineC.getKey();

			// for each N-gram in a line
			for (int i = 0; i < line.length() - (ngram_size - 1); i++) {
				// build an N-gram of ngram_size
				substr = line.substring(i, i + ngram_size);
				// if the N-gram is the first
				if (ngram == null) {
					// build the N-gram as an object with its line number
					ngram = new Ngram(substr, line_number);
					// put the ngram into the hash map with the relevent N-gram ID
					storage_map.put(substr + 0, ngram);
					// set the N-grams ID
					ngram.setId(0);
				}
				// if at least 1 N-gram already exists
				else {
					// create the next N-gram object with its line number
					Ngram temp = new Ngram(substr, line_number);
					// set temp as the next N-gram in the order
					ngram.setNextNgram(temp);
					// update the current N-gram position
					ngram = temp;
					// if the hashmap does not already contain this N-gram
					if (!storage_map.containsKey(substr + 0)) {
						// add N-gram to the hashmap
						storage_map.put(substr + 0, ngram);
						// set the N-grams ID
						ngram.setId(0);
						// if the N-gram already exists in the hashmap
					}
					else {
						// find an id not used by the N-gram
						// there must be a better way to do this? (might just require building a custom map where each key holds a list)
						int j = 1;
						while (storage_map.containsKey(substr + j)) {
							j++;
						}
						// add N-gram to the hashmap
						storage_map.put(substr + j, ngram);
						// set N-grams ID
						ngram.setId(j);
					}
				}
			}
		}
	}

	/**
	 * The main processing method used in the detector
	 */
	public class NGramDetectorWorker extends PairwiseDetectorWorker<NGramRawResult> {

		public NGramDetectorWorker(IDetector parent, ModelDataItem file1Data, ModelDataItem file2Data) {
			super(parent, file1Data, file2Data);
		}

		/**
		 *
		 */
		@Override
		public void execute() {

			// Gets each line as a string in the list, as returned by the specified preprocessor
			ArrayList<IndexedString> linesF1 = new ArrayList<IndexedString>(this.file1.getPreProcessedLines("no_whitespace"));
			ArrayList<IndexedString> linesF2 = new ArrayList<IndexedString>(this.file2.getPreProcessedLines("no_whitespace"));

			// make raw result output container
			res = new NGramRawResult<>(this.file1.getFile(), this.file2.getFile());

			// generate the N-grams for file 1 and load them into a hash map
			HashMap<String, Ngram> storage_map = new HashMap<String, Ngram>();
			loadNgramMap(storage_map, linesF1);
			// generate the N-grams for file 2 and load them into a list
			ArrayList<Ngram> storage_list = new ArrayList<Ngram>();
			loadNgramList(storage_list, linesF2);

			// start of file check
			Ngram substrObj;
			ArrayList<Ngram> reference = new ArrayList<Ngram>();
			ArrayList<Ngram> check = new ArrayList<Ngram>();
			Ngram head = null;

			// the value of similarity the last peak held
			float last_peak = 1.0f;
			// the last val for similarity held
			float last_val = 0.0f;
			// the val for similarity held
			float sim_val = 1.0f;
			// the number of steps made since the last time last peak was updated
			int since_last_peak = 0;

			// the counter for duplicate N-grams. Used to keep track of which version is being refered to for comparison
			int ngram_id = 0;

			// the check N-gram string
			String ngram_string;

			for (int i = 0; i < storage_list.size(); i++) {
				// acquire ngram
				substrObj = storage_list.get(i);
				// get N-gram string
				ngram_string = substrObj.getNgram();

				// if the previous file has a matching ngram (id references the occurrence of said ngram if there are duplicates)
				if (storage_map.containsKey(ngram_string + ngram_id) || reference.size() > 0) {
					// build up a window and threshold similarity
					// if over threshold keep increasing window by 1 until similarity drops bellow threshold

					// if head is null we are starting a new comparison check
					if (head == null) {
						// set head to the start of the sequence in the reference file
						head = storage_map.get(ngram_string + ngram_id);
						// add the reference start to the reference list
						reference.add(head);
					}
					// otherwise we update reference and head
					else {
						// get the next ngram in the reference sequence
						head = head.getNextNgram();
						// if sequence has ended
						if (head == null) {
							// EOF in reference reached, abandon loop and then check for match (post loop check)
							break;
						}
						// add next in sequence to list
						else {
							reference.add(head);
						}

					}
					// add the N-gram to check
					check.add(substrObj);

					// update peak data
					// this allows retraction to last peak in the case of the similarity falling below the threshold
					// this prevents detection bleeding
					since_last_peak++;
					// compare the two lists
					sim_val = compare(reference, check);
					// if the similarity has risen we have a new peak
					if (sim_val >= last_val) {
						since_last_peak = 0;
						last_peak = sim_val;
					}
					// update last val for use in next iteration
					last_val = sim_val;

					// nothing substantial has flagged, reset lists
					if (reference.size() == minimum_window && sim_val < threshold) {
						// if another case of the starting N-gram exists in the other file move to that and reperform the check
						if (storage_map.containsKey(reference.get(0).getNgram() + (ngram_id + 1))) {
							// move file position back to appropriate N-gram
							i -= minimum_window;
							ngram_id++;
						}
						// empty lists
						reference.clear();
						check.clear();
						head = null;
						// reset peak value trackers
						since_last_peak = 0;
						last_val = 0.0f;
					}
					// when the window is over the minimum and drops below the threshold
					else if (reference.size() > minimum_window && sim_val < threshold) {
						//						// send the data to construct a match object for the found match
						matchFound(reference, check, head, last_peak, since_last_peak, this.file1.getFile(), this.file2.getFile());
						// reset duplicate ngram ID
						ngram_id = 0;
						// set head to null so a new reference can be made
						head = null;
						// reset peak value trackers
						since_last_peak = 0;
						last_val = 0.0f;
					}
				}
			}
			// performs comparison if EOF for reference is reached
			if (compare(reference, check) > threshold && reference.size() >= minimum_window) {
				// if at EOF there is a match then output it
				matchFound(reference, check, head, last_peak, since_last_peak, this.file1.getFile(), this.file2.getFile());
			}

			// data of type Serializable, essentially raw data stored as a variable.
			this.result = res;
		}
	}

	/**
	 * Object to store N-Gram data in a refined structure.
	 */
	class Ngram {

		/**
		 * The N-Gram itself in string form.
		 */
		private String segment;
		/**
		 * The line the N-Gram starts on.
		 */
		private int line_number;
		/**
		 * The id of the N-Gram to allow differentiation of duplicates.
		 */
		private int id;

		/**
		 * Linked List pointer to allow the next N-Gram in a reference file to be found when the start is acquired from a hashmap.
		 */
		private Ngram next_ngram;

		/**
		 * Object constructor.
		 *
		 * @param segment     The N-Gram being stored.
		 * @param line_number The line number the N-Gram starts on.
		 */
		public Ngram(String segment, int line_number) {
			this.segment = segment;
			this.line_number = line_number;
		}

		/**
		 * Checks if 2 N-Grams are the same string.
		 *
		 * @param ngram The N-Gram to compare to.
		 *
		 * @return True if strings are equal, false otherwise.
		 */
		public boolean equals(Ngram ngram) {
			return this.segment.equals(ngram.getNgram());
		}

		/**
		 * @return The version ID of this N-Gram.
		 */
		public int getId() {
			return id;
		}

		/**
		 * @param id The ID to allow duplicates to exist.
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * @return The line number at the start of the N-Gram.
		 */
		public int getLineNumber() {
			return line_number;
		}

		/**
		 * @return The next N-Gram in the file.
		 */
		public Ngram getNextNgram() {
			return next_ngram;
		}

		/**
		 * @param ngram The next N-Gram in the file.
		 */
		public void setNextNgram(Ngram ngram) {
			next_ngram = ngram;
		}

		/**
		 * @return The N-Gram string.
		 */
		public String getNgram() {
			return segment;
		}

	}
}

// NOTE this will give the one way comparison, to get the other direction it must be run with the files reversed

// TODO finish commenting
// TODO clean
