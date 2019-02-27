package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;

public class DetectionType {

	private String identifier, displayName, reason;
	private float weighting;

	public DetectionType(String identifier, String displayName, String reason, float weighting) {
		this.identifier = identifier;
		this.displayName = displayName;
		this.reason = reason;
		this.weighting = weighting;
	}

	public static void addDefaultDetectionTypes() {
		SherlockRegistry.registerDetectionType(new DetectionType("BASE_COMMENT", "Comment", "The specified comments are identical to each other.", 1f));
		SherlockRegistry.registerDetectionType(new DetectionType("BASE_VARIABLE_NAME", "Variable Name", "This variable has been used in the same way in each file, but has a different name.", 1f));
		SherlockRegistry.registerDetectionType(new DetectionType("BASE_METHOD_NAME", "Method Name", "The methods in each file are identical in content but have different names.", 1f));
		SherlockRegistry.registerDetectionType(new DetectionType("BASE_COPIED_BLOCK", "Copied Block", "The blocks in these files are completely identical to each other.", 1f));
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public float getWeighting() {
		return weighting;
	}

	public void setWeighting(float weighting) {
		this.weighting = weighting;
	}

	@Override
	public String toString() {
		return String.format("<%s> %s[%f] - %s", this.identifier, this.displayName, this.weighting, this.reason);
	}
}
