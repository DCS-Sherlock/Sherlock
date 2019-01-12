package uk.ac.warwick.dcs.sherlock.api.annotation;

import uk.ac.warwick.dcs.sherlock.api.SherlockHelper;

import java.lang.reflect.Field;

public class AdjustableParameterObj {

	private AdjustableParameter param;
	private String reference;
	private boolean isInt;

	public AdjustableParameterObj(AdjustableParameter param, Field field) {
		this.param = param;
		this.reference = SherlockHelper.buildFieldReference(field);
		this.isInt = field.getType().equals(int.class);
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
	 * @return The maximum bound for the field
	 */
	public float getMaxumumBound() {
		return this.param.maxumumBound();
	}

	/**
	 * @return Minimum bound for field
	 */
	public float getMinimumBound() {
		return this.param.minimumBound();
	}

	/**
	 * @return Name for the parameter to be displayed in the UI
	 */
	public String getName() {
		return this.param.name();
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
	 * @return is the parameter an integer parameter? (if not is a float)
	 */
	public boolean isInt() {
		return isInt;
	}
}
