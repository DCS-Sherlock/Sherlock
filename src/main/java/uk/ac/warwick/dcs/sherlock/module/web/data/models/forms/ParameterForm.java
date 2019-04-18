package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import org.springframework.validation.BindingResult;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.DetectorNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.DetectorWrapper;

import java.util.*;

/**
 * The form for changing parameters on a template detector
 */
public class ParameterForm {

    private Map<String, Float> parameters = new HashMap<>();
    private Map<String, Float> postprocessing = new HashMap<>();

    public ParameterForm() {}

    public ParameterForm(DetectorWrapper detectorWrapper) throws DetectorNotFound {
        List<AdjustableParameterObj> parameterObjList = detectorWrapper.getEngineParameters();
        List<AdjustableParameterObj> postprocessingObjList = detectorWrapper.getEnginePostProcessingParameters();

        Map<String, Float> parameterMap = new HashMap<>();
        Map<String, Float> postprocessingMap = new HashMap<>();
        detectorWrapper.getDetector().getDetectorParameters().forEach(p -> parameterMap.put(p.getName(), p.getValue()));
        detectorWrapper.getDetector().getPostParameters().forEach(p -> postprocessingMap.put(p.getName(), p.getValue()));

        parameters = new HashMap<>();
        for (AdjustableParameterObj obj : parameterObjList) {
            if (parameterMap.containsKey(obj.getName())) {
                parameters.put(obj.getName(), parameterMap.get(obj.getName()));
            } else {
                parameters.put(obj.getName(), obj.getDefaultValue());
            }
        }

        postprocessing = new HashMap<>();
        for (AdjustableParameterObj obj : postprocessingObjList) {
            if (postprocessingMap.containsKey(obj.getName())) {
                postprocessing.put(obj.getName(), postprocessingMap.get(obj.getName()));
            } else {
                postprocessing.put(obj.getName(), obj.getDefaultValue());
            }
        }
    }

    public Map<String, Float> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Float> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Float> getPostprocessing() {
        return postprocessing;
    }

    public void setPostprocessing(Map<String, Float> parameters) {
        this.postprocessing = parameters;
    }

    public BindingResult validate(BindingResult result, List<AdjustableParameterObj> parameterObjList, List<AdjustableParameterObj> postprocessingObjList) {
        result = validate(result, parameterObjList, false);
        result = validate(result, postprocessingObjList, true);
        return result;
    }

    /**
     * Loops through all of the parameters in the form to check the following:
     * - That the parameter exists for the detector
     * - That the min bound, max bound and step restrictions are respected
     * If any conditions are not met, add errors to the binding result object
     *
     * @param result the result holder for a DataBinder
     * @param post
     * @param parameterObjList the list of parameters to validate the form against
     *
     * @return the updated results holder
     */
    public BindingResult validate(BindingResult result, List<AdjustableParameterObj> parameterObjList, boolean post) {
        Map<String, AdjustableParameterObj> map = new HashMap<>();

        Map<String, Float> floatMap = parameters;
        if (post) {
            floatMap = postprocessing;
        }

        for (AdjustableParameterObj obj : parameterObjList) {
            map.put(obj.getName(), obj);

            if (!floatMap.containsKey(obj.getName())) {
                floatMap.put(obj.getName(), obj.getDefaultValue());
            }
        }

        for (Map.Entry<String, Float> entry : floatMap.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                floatMap.remove(entry.getKey());
            }

            if (entry.getValue() < map.get(entry.getKey()).getMinimumBound()) {
                result.reject("templates.parameters.min_warning");
            }

            if (entry.getValue() > map.get(entry.getKey()).getMaximumBound()) {
                result.reject("templates.parameters.max_warning");
            }

            float step = entry.getValue() % map.get(entry.getKey()).getStep();
            float threshold = 0.001f;
            if (step > threshold) {
                result.reject("templates.parameters.step_warning");
            }
        }

        if (post) {
            postprocessing = floatMap;
        } else {
            parameters = floatMap;
        }

        return result;
    }
}
