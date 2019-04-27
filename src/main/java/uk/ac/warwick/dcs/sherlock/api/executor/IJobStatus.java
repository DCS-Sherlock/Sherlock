package uk.ac.warwick.dcs.sherlock.api.executor;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;

public interface IJobStatus extends Comparable<IJobStatus> {

	@Override
	int compareTo(@NotNull IJobStatus o);

	Duration getDuration();

	String getFormattedDuration();

	int getId();

	String getMessage();

	float getProgress();

	int getProgressInt();

	Instant getStartTime();

	int getStep();

	boolean isFinished();
}
