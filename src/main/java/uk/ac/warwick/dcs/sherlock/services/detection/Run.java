/**
 * 
 */
package uk.ac.warwick.dcs.sherlock.services.detection;

/**
 * Holds the start and end lines for both files which match
 * 
 * @author Aliyah
 */
class Run {
	private LineNumber start;
	private LineNumber end;
	private int runLength;

	LineNumber getStart() {
		return start;
	}

	void setStart(LineNumber start) {
		this.start = start;
	}

	LineNumber getEnd() {
		return end;
	}

	void setEnd(LineNumber end) {
		this.end = end;
	}

	int getRunLength() {
		return runLength;
	}

	/**
	 * Set the length of this run instance 
	 * @param runLength
	 */
	private void setRunLength(int runLength) {
		this.runLength = runLength;
	}
	
	Run(LineNumber start, LineNumber end, int runLength){
		this.start = start;
		this.end = end;
		this.runLength = runLength;
	}
}