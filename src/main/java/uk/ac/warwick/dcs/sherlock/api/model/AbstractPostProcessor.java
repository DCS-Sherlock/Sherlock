package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.model.data.IWorkspace;
import uk.ac.warwick.dcs.sherlock.api.model.data.ModelProcessedResults;

import java.io.Serializable;
import java.lang.annotation.*;

public abstract class AbstractPostProcessor implements Serializable {

	private static final long serialversionUID = 24L;

	private long taskId;
	private IWorkspace workspace;
	private Class<? extends IDetector> detector;
	private IDetector.Rank rank;

	public AbstractPostProcessor(long taskId, IWorkspace workspace, Class<? extends IDetector> detector, IDetector.Rank rank) {
		this.taskId = taskId;
		this.workspace = workspace;
		this.detector = detector;
		this.rank = rank;
	}

	public Class<? extends IDetector> getDetector() {
		return detector;
	}

	public IDetector.Rank getRank() {
		return rank;
	}

	public long getTaskId() {
		return taskId;
	}

	public IWorkspace getWorkspace() {
		return workspace;
	}

	/**
	 * Run the post processing and return a data item with the final results in the correct format
	 *
	 * @return
	 */
	abstract ModelProcessedResults processResults();

	/**
	 * Annotation to define a parameter as adjustable by the UI. Currently must be a float or int.
	 * <p><p>
	 * If another type is required please request it on https://github.com/DCS-Sherlock/Sherlock/issues
	 * <p><p>
	 * Set the parameter declaration to the desired default value
	 * <p><p>
	 * The engine will set this parameter to it's adjusted value when creating an instance of an IDetector implementatiomn
	 */
	@Documented
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.FIELD)
	@interface PostProcessParameter {

		/**
		 * Default value the parameter takes
		 */
		float defaultValue();

		/**
		 * Optional, detailed description of what the parameter does
		 */
		String description() default "";

		/**
		 * The maximum bound for the field
		 */
		float maxumumBound();

		/**
		 * Minimum bound for field
		 */
		float minimumBound();

		/**
		 * Name for the parameter to be displayed in the UI
		 */
		String name();

		/**
		 * The step to increment or decrement the parameter by in the UI
		 */
		float step();
	}
}
