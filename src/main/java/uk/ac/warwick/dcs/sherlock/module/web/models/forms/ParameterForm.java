package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import org.springframework.validation.BindingResult;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterForm {

    public Map<String, Float> parameters = new HashMap<>();

    public ParameterForm() {}

    public ParameterForm(List<AdjustableParameterObj> parameterObjList) {
        parameters = new HashMap<>();
        for (AdjustableParameterObj obj : parameterObjList) {
            parameters.put(obj.getName(), obj.getDefaultValue());
        }
    }

    public Map<String, Float> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Float> parameters) {
        this.parameters = parameters;
    }

    public BindingResult validate(BindingResult result, List<AdjustableParameterObj> parameterObjList) {
        List<String> validKeys = new ArrayList<>();

        for (AdjustableParameterObj obj : parameterObjList) {
            validKeys.add(obj.getName());

            if (!parameters.containsKey(obj.getName())) {
                parameters.put(obj.getName(), obj.getDefaultValue());
            }
        }

        for (Map.Entry<String, Float> entry : parameters.entrySet()) {
            if (!validKeys.contains(entry.getKey())) {
                parameters.remove(entry.getKey());
            }

            //TODO: check step, min and max
        }

        return result;
    }
}
