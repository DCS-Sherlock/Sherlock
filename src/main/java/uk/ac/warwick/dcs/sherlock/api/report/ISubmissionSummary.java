package uk.ac.warwick.dcs.sherlock.api.report;

import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

public interface ISubmissionSummary {

	long getPersistentId();

	float getScore();

	List<ITuple<Long, Float>> getMatchingSubmissions();
}
