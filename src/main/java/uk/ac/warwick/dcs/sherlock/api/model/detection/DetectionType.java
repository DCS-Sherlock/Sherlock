package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;

/**
 * Class to specify an individual detection type, these are used to mark detected blocks, so that verbose reports can be generated
 */
public class DetectionType {

	private String identifier, displayName, reason;
	private double weighting;

	public DetectionType(String identifier, String displayName, String reason, double weighting) {
		this.identifier = identifier;
		this.displayName = displayName;
		this.reason = reason;
		this.weighting = weighting;
	}

	/**
	 * Default types included with the API
	 */
	public static void addDefaultDetectionTypes() {
		SherlockRegistry.registerDetectionType(new DetectionType("BASE_COMMENT", "Comment", "The specified comments are identical to each other.", 0.4));
		SherlockRegistry.registerDetectionType(new DetectionType("BASE_VARIABLE_NAME", "Variable Name", "This variable has been copied between each file.", 0.1));
		SherlockRegistry.registerDetectionType(new DetectionType("BASE_METHOD_NAME", "Method Name", "The methods in each file are identical in content but have different names.", 0.8));
		SherlockRegistry.registerDetectionType(new DetectionType("BASE_COPIED_BLOCK", "Copied Block", "The blocks in these files are completely identical to each other.", 1.0));
		SherlockRegistry.registerDetectionType(new DetectionType("BASE_BODY_REPLACE_CALL", "Function Body replace Call", "The code in these sections are effectively identical; the function called in one file is identical to the highlighted section in the other.", 0.6));
	}

	/**
	 * Display name getter
	 * @return the types display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Display name setter
	 * @param displayName end user readable name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Unique string identifier getter
	 * @return Unique string identifier for the type
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Unique string identifier setter
	 * @param identifier unique string identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Verbose reason blocks was detected getter
	 * @return reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Verbose reason block was detected setter
	 * @param reason reason string
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * Getter - Weighting of the type for overall scoring, tuned to adjust the impact of each type to give a balanced overall result and report
	 * @return weighting
	 */
	public double getWeighting() {
		return weighting;
	}

	/**
	 * Setter - Weighting of the type for overall scoring, tuned to adjust the impact of each type to give a balanced overall result and report
	 *
	 * @param weighting weighting
	 */
	public void setWeighting(float weighting) {
		this.weighting = weighting;
	}

	/**
	 * Constructs string representation of this type
	 * @return string representation
	 */
	@Override
	public String toString() {
		return String.format("<%s> %s[%f] - %s", this.identifier, this.displayName, this.weighting, this.reason);
	}
}
