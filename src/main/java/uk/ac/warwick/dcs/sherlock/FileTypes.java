/**
 * 
 */
package uk.ac.warwick.dcs.sherlock;

/**
 * @author Aliyah
 *
 *	The possible file types to be used in the detection strategy
 */
public enum FileTypes {
	/**
	 * Numeric value for the original.
	 */
	ORI(0),
	
	/**
	 * Numeric value for no-whitespace.
	 */
	NWS(1),

	/**
	 * Numerical value for no comment no white.
	 */
	NCW(2),

	/**
	 * Numeric value for no comment.
	 */
	NOC(3),

	/**
		* Numeric value for comment.
		*/
	COM(4),
	
	/**
	 * Numeric value for the tokenised files.
	 */
	TOK(5),
	
	/**
	 * Numeric value for the whitespace pattern.
	 */
	WSP(6);
	
	private int value;
	
	FileTypes(int value) {
		this.value = value;
	}
	
	public int getValue() {
        return value;
    }
	
	static int getNumberOfFileTypes() {
		return values().length ;
	}
}
