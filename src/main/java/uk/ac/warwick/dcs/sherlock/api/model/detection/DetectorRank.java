package uk.ac.warwick.dcs.sherlock.api.model.detection;

/**
 * Enum to specifiy the "rank" of a detector.
 * <br><br>
 * - Primary detectors can be used on their own to draw conclusions
 * <br>
 * - Supporting detectors are used to verify and support the findings of primary detectors. On their own they are not indicative of plagiarism (for example duplicated variable names)
 */
public enum DetectorRank {
	PRIMARY, SUPPORTING
}
