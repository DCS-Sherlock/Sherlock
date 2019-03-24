package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.ParameterNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TParameter;

import java.util.Map;

public class ParameterWrapper {
    private TParameter tParameter;
    private AdjustableParameterObj parameterObj;

    public ParameterWrapper(TParameter tParameter, Map<String, AdjustableParameterObj> map) throws ParameterNotFound {
        this.tParameter = tParameter;

        if (!map.containsKey(tParameter.getName())) {
            throw new ParameterNotFound("Parameter not found");
        }

        this.parameterObj = map.get(tParameter.getName());
    }

    public TParameter getParameter() {
        return tParameter;
    }

    public void setParameter(TParameter tParameter) {
        this.tParameter = tParameter;
    }

    public AdjustableParameterObj getParameterObj() {
        return parameterObj;
    }

    public void setParameterObj(AdjustableParameterObj parameterObj) {
        this.parameterObj = parameterObj;
    }
}
