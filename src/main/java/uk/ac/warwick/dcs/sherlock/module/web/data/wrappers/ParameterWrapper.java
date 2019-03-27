package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.ParameterNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TParameter;

import java.util.Map;

/**
 * The wrapper that manages template parameters
 */
public class ParameterWrapper {
    /**
     * The parameter database entity
     */
    private TParameter tParameter;

    /**
     * The adjustable parameter object
     */
    private AdjustableParameterObj parameterObj;

    /**
     * Initialise the wrapper class using the parameter entity
     *
     * @param tParameter the parameter to manage
     * @param map the adjustable parameter map for the detector
     *
     * @throws ParameterNotFound if the parameter was not found in the map
     */
    public ParameterWrapper(TParameter tParameter, Map<String, AdjustableParameterObj> map) throws ParameterNotFound {
        this.tParameter = tParameter;

        if (!map.containsKey(tParameter.getName())) {
            throw new ParameterNotFound("Parameter not found");
        }

        this.parameterObj = map.get(tParameter.getName());
    }

    /**
     * Get the database parameter entity
     *
     * @return the parameter
     */
    public TParameter getParameter() {
        return tParameter;
    }

    /**
     * Set the database parameter entity for this wrapper
     *
     * @param tParameter the new parameter
     */
    public void setParameter(TParameter tParameter) {
        this.tParameter = tParameter;
    }

    /**
     * Get the adjustable parameter object
     *
     * @return the parameter object
     */
    public AdjustableParameterObj getParameterObj() {
        return parameterObj;
    }

    /**
     * Set the adjustable parameter object
     *
     * @param parameterObj the new object
     */
    public void setParameterObj(AdjustableParameterObj parameterObj) {
        this.parameterObj = parameterObj;
    }
}
