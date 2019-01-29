package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import org.springframework.validation.BindingResult;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.DetectorNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.DetectorWrapper;

import java.util.*;

public class ParameterForm {

    public Map<String, Float> parameters = new HashMap<>();

    public ParameterForm() {}

    public ParameterForm(DetectorWrapper detectorWrapper) throws DetectorNotFound {
        List<AdjustableParameterObj> parameterObjList = detectorWrapper.getEngineParameters();

        Map<String, Float> parameterMap = new HashMap<>();
        detectorWrapper.getDetector().getParameters().forEach(p -> parameterMap.put(p.getName(), p.getValue()));

        parameters = new HashMap<>();
        for (AdjustableParameterObj obj : parameterObjList) {
            if (parameterMap.containsKey(obj.getName())) {
                parameters.put(obj.getName(), parameterMap.get(obj.getName()));
            } else {
                parameters.put(obj.getName(), obj.getDefaultValue());
            }
        }
    }

    public Map<String, Float> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Float> parameters) {
        this.parameters = parameters;
    }

    public BindingResult validate(BindingResult result, List<AdjustableParameterObj> parameterObjList) {
        Map<String, AdjustableParameterObj> map = new HashMap<>();

        for (AdjustableParameterObj obj : parameterObjList) {
            map.put(obj.getName(), obj);

            if (!parameters.containsKey(obj.getName())) {
                parameters.put(obj.getName(), obj.getDefaultValue());
            }
        }

        for (Map.Entry<String, Float> entry : parameters.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                parameters.remove(entry.getKey());
            }

            if (entry.getValue() < map.get(entry.getKey()).getMinimumBound()) {
                result.reject("templates_parameter_min");
            }

            if (entry.getValue() > map.get(entry.getKey()).getMaximumBound()) {
                result.reject("templates_parameter_max");
            }

            float step = entry.getValue() % map.get(entry.getKey()).getStep();
            float threshold = 0.001f;
            if (step > threshold) {
                result.reject("templates_parameter_step");
            }
        }

        return result;
    }
}
