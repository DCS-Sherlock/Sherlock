/**
 * 
 */
package sherlock.model.analysis.detection;

/**
 * This class holds the pair of line numbers for the start or end of a run for both files in a matching pair.
 * @author Aliyah
 *
 */
public class LineNumber {

	/**
	 * 	Line number for the first file in the match
	 */
	private int lineFile1;
	
	/**
	 * 	Line number for the second file in the match
	 */
	private int lineFile2;
	
	
	public LineNumber( int lineFile1, int lineFile2 ) {
		this.lineFile1 = lineFile1;
		this.lineFile2 = lineFile2;
	}

	/**
	 * Return the line number for file 1 in the match
	 * @return		
	 */
	int getLineFile1() {
		return lineFile1;
	}

	/**
	 * Return the line number for file 2 in the match
	 * @return		
	 */
	int getLineFile2() {
		return lineFile2;
	}
	
}
