package uk.ac.warwick.dcs.sherlock.api.annotation;

import uk.ac.warwick.dcs.sherlock.api.util.SherlockHelper;

import java.lang.reflect.Field;

/**
 * An object to wrap an adjustable parameter annotation with other information commonly required when working with it
 */
public class AdjustableParameterObj {

	private AdjustableParameter param;
	private String name;
	private String reference;
	private boolean isInt;
	private boolean isFixed;

	public AdjustableParameterObj(AdjustableParameter param, Field field, boolean isFixed) {
		this.param = param;
		this.name = field.getName();
		this.reference = SherlockHelper.buildFieldReference(field);
		this.isInt = field.getType().equals(int.class);
		this.isFixed = isFixed;
	}

	/**
	 * Default value the parameter takes
	 *
	 * @return the default value
	 */
	public float getDefaultValue() {
		return this.param.defaultValue();
	}

	/**
	 * Optional, detailed description of what the parameter does
	 *
	 * @return the description string
	 */
	public String getDescription() {
		return this.param.description();
	}

	/**
	 * Display Name for the parameter to be displayed in the UI
	 *
	 * @return the display name
	 */
	public String getDisplayName() {
		return this.param.name();
	}

	/**
	 * The maximum bound for the field
	 *
	 * @return the max bound
	 */
	public float getMaximumBound() {
		return this.param.maxumumBound();
	}

	/**
	 * Minimum bound for field
	 *
	 * @return the min bound
	 */
	public float getMinimumBound() {
		return this.param.minimumBound();
	}

	/**
	 * Field Name for the parameter for use in the UI backend
	 *
	 * @return backend name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Fetches the reference string for the adjustable parameter represented by this object
	 *
	 * @return the parameter specific string
	 */
	public String getReference() {
		return this.reference;
	}

	/**
	 * The step to increment or decrement the parameter by in the UI
	 *
	 * @return the step value
	 */
	public float getStep() {
		return this.param.step();
	}

	/**
	 * Return if the parameter is fixed, ie. it cannot be modified after a task has been run, for example when it is for a detector
	 *
	 * @return is the param fixed after raw results have been generated?
	 */
	public boolean isFixed() {
		return isFixed;
	}

	/**
	 * is the parameter an integer parameter? (if not is a float)
	 *
	 * @return is an integer?
	 */
	public boolean isInt() {
		return isInt;
	}
}
