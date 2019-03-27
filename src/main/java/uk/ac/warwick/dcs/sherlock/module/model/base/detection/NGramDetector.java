package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.detection.AbstractPairwiseDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.AbstractPairwiseDetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectorRank;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NGramDetector.NGramDetectorWorker;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.NGramRawResult;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.TrimWhitespaceOnly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class NGramDetector extends AbstractPairwiseDetector<NGramDetectorWorker> {

	NGramRawResult<NgramMatch> res;

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

	// presumably for use in threading (check with james)
	@Override
	public NGramDetectorWorker getAbstractPairwiseDetectorWorker() {
		return new NGramDetectorWorker();
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
			// if line is shorter than the ngram_size skip it
			if (line.length() < ngram_size) {
				continue;
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
			// if line is shorter than the ngram_size skip it
			if (line.length() < ngram_size) {
				continue;
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
		// calcualte and return the Jaccard Similarity
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
			NgramMatch temp = new NgramMatch(reference.get(0).getLineNumber(), reference.get(reference.size() - 1).getLineNumber(),
					check.get(0).getLineNumber(), check.get(check.size() - 1).getLineNumber(), last_peak, file1, file2);
			// put an N-gram match into res along wih the start points of the segment in reference file then checked file.
			res.put(temp, reference.get(0).getLineNumber(), reference.get(reference.size() - 1).getLineNumber(), check.get(0).getLineNumber(), check.get(check.size() - 1).getLineNumber());
		}
		reference.clear();
		check.clear();
		//		head = null;
//		System.out.println("matchFound");
	}

	/**
	 * Returns the detectors name for display purposes.
	 *
	 * @return A string name for the detector object.
	 */
	@Override
	public String getDisplayName() {
		return "N-Gram Detector";
	}

	/**
	 * Gets the set of preprocessors to be used in detection
	 *
	 * @return A list of preprocessors to be used in detection
	 */
	@Override
	public List<PreProcessingStrategy> getPreProcessors() {
		return Collections.singletonList(PreProcessingStrategy.of("no_whitespace", TrimWhitespaceOnly.class));
	}

	/**
	 * @return
	 */
	@Override
	public DetectorRank getRank() {
		return DetectorRank.PRIMARY;
	}

	/**
	 * The main processing method used in the detector
	 */
	public class NGramDetectorWorker extends AbstractPairwiseDetectorWorker<NGramRawResult> {

		/**
		 *
		 */
		@Override
		public void execute() {

			// Gets each line as a string in the list, as returned by the specified preprocessor
			ArrayList<IndexedString> linesF1 = new ArrayList<IndexedString>(this.file1.getPreProcessedLines("no_whitespace"));
			ArrayList<IndexedString> linesF2 = new ArrayList<IndexedString>(this.file2.getPreProcessedLines("no_whitespace"));

			// make
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
				// aquire ngram
				substrObj = storage_list.get(i);
				// get N-gram string
				ngram_string = substrObj.getNgram();

				if (storage_map.containsKey(ngram_string + ngram_id) || reference.size() > 0) {
					// build up a window and threshold similarity
					// if over threshold keep increasing window by 1 until similarity drops bellow threshold
					// if possible when value falls bellow threshold shrink window to last value before value starts to decrease (final peak)
					// System.out.println(storage_map.get(substr + 0));
					if (head == null) {
						head = storage_map.get(ngram_string + ngram_id);
						reference.add(head);
					}
					else {
						head = head.getNextNgram();
						if (head == null) {
							// EOF in reference reached, abandon loop and then check for match (post loop check)
							break;
						}
						else {
							reference.add(head);
						}

					}
					// add the N-gram to check
					check.add(substrObj);

					// update peak data
					since_last_peak++;
					sim_val = compare(reference, check);
					// System.out.println(sim_val);
					if (sim_val >= last_val) {
						since_last_peak = 0;
						last_peak = sim_val;
					}
					last_val = sim_val;

					// nothing substantial has flagged, reset lists
					if (reference.size() == minimum_window && sim_val < threshold) {
//						System.out.println("miss");
						// if another case of the starting N-gram exists in the other file move to that and reperform the check
						if (storage_map.containsKey(reference.get(0).getNgram() + (ngram_id + 1))) {
							// move file position back to apropriete N-gram
							i -= minimum_window;
							ngram_id++;
						}
						reference.clear();
						check.clear();
						head = null;
						// reset peak value trackers
						since_last_peak = 0;
						last_val = 0.0f;                    // if value drops window has reached max useful length
					}
					else if (reference.size() > minimum_window && sim_val < threshold) {
//						System.out.println("hit1");
						matchFound(reference, check, head, last_peak, since_last_peak, this.file1.getFile(), this.file2.getFile());
						ngram_id = 0;
						// set head to null so a new reference can be made
						head = null;
						// reset peak value trackers
						since_last_peak = 0;
						last_val = 0.0f;
					}
				}
			}
			if (compare(reference, check) > threshold) {
//				System.out.println("hit2");
				// if at EOF there is a match then output it
				matchFound(reference, check, head, last_peak, since_last_peak, this.file1.getFile(), this.file2.getFile());
				ngram_id = 0;
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
		 * @param segment The N-Gram being stored.
		 * @param line_number	The line number the N-Gram starts on.
		 */
		public Ngram(String segment, int line_number) {
			this.segment = segment;
			this.line_number = line_number;
		}

		/**
		 * Checks if 2 N-Grams are the same string.
		 * @param ngram The N-Gram to compare to.
		 * @return True if strings are equal, false otherwise.
		 */
		public boolean equals(Ngram ngram) {
			return this.segment == ngram.getNgram();
		}

		/**
		 * @return The N-Gram string.
		 */
		public String getNgram() {
			return segment;
		}

		/**
		 * @return The line number at the start of the N-Gram.
		 */
		public int getLineNumber() {
			return line_number;
		}

		/**
		 * @param id The ID to allow duplicates to exist.
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * @return The version ID of this N-Gram.
		 */
		public int getId() {
			return id;
		}

		/**
		 * @param ngram The next N-Gram in the file.
		 */
		public void setNextNgram(Ngram ngram) {
			next_ngram = ngram;
		}

		/**
		 * @return The next N-Gram in the file.
		 */
		public Ngram getNextNgram() {
			return next_ngram;
		}

	}
}

// NOTE this will give the one way comparison, to get the other direction it must be run with the files reversed

// TODO prevent back to peak going bellow min window value
// TODO finish commenting
