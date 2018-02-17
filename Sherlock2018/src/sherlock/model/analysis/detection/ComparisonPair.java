/**
 * 
 */
package sherlock.model.analysis.detection;

import java.io.File;
import java.util.*;

/**
 * Holds the detection comparison result for a pair of distinct input files.
 * @author Aliyah
 *
 */
public class ComparisonPair {
	private File f1;
	private File f2;
	
	private List<Run> runs = new LinkedList<Run>();
	private Run maxRun ;
	
	private int similarityValue;
	
	/**
	 * @param f1		- The first file
	 * @param f2		- The second file
	 */
	protected ComparisonPair(File f1, File f2) {
		this.f1 = f1;
		this.f2 = f2;
	}

	Run getMaxRun() {
		return maxRun;
	}

	private void setMaxRun(Run maxRun) {
		this.maxRun = maxRun;
	}

	List<Run> getRuns() {
		return runs;
	}

	int getSimilarityValue() {
		return similarityValue;
	}
	
}