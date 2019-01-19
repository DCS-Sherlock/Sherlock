package uk.ac.warwick.dcs.sherlock.api.annotation;

import uk.ac.warwick.dcs.sherlock.api.SherlockHelper;

import java.lang.reflect.Field;

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
	 * @return Default value the parameter takes
	 */
	public float getDefaultValue() {
		return this.param.defaultValue();
	}

	/**
	 * @return Optional, detailed description of what the parameter does
	 */
	public String getDescription() {
		return this.param.description();
	}

	/**
	 * @return Display Name for the parameter to be displayed in the UI
	 */
	public String getDisplayName() {
		return this.param.name();
	}

	/**
	 * @return The maximum bound for the field
	 */
	public float getMaximumBound() {
		return this.param.maxumumBound();
	}

	/**
	 * @return Minimum bound for field
	 */
	public float getMinimumBound() {
		return this.param.minimumBound();
	}

	/**
	 * @return Field Name for the parameter for use in the UI backend
	 */
	public String getName() {
		return this.name;
	}

	public String getReference() {
		return this.reference;
	}

	/**
	 * @return The step to increment or decrement the parameter by in the UI
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
	 * @return is the parameter an integer parameter? (if not is a float)
	 */
	public boolean isInt() {
		return isInt;
	}
}
