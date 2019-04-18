package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The wrapper that manages the task
 */
public class TaskWrapper {
    /**
     * The task entity
     */
    private ITask task;

    /**
     * The detector wrapper
     */
    private EngineDetectorWrapper detectorWrapper;

    /**
     * Initialise the wrapper using the task supplied
     *
     * @param task the task to manage
     */
    public TaskWrapper(ITask task) {
        this.task = task;
        this.detectorWrapper = new EngineDetectorWrapper(this.task.getDetector());
    }

    /**
     * Get the display name of the detector
     *
     * @return the name
     */
    public String getDisplayName() {
        return this.detectorWrapper.getDisplayName();
    }

    /**
     * Get the list of parameters as a string
     *
     * @return the string result
     */
    public String getParameterString() {
        String result = "";

        List<AdjustableParameterObj> parameters = SherlockRegistry.getDetectorAdjustableParameters(task.getDetector());
        List<AdjustableParameterObj> postprocessing = SherlockRegistry.getPostProcessorAdjustableParametersFromDetector(task.getDetector());

        for (Map.Entry<String, Float> entry : task.getParameterMapping().entrySet()) {
            List<AdjustableParameterObj> para = new ArrayList<>();
            List<AdjustableParameterObj> post = new ArrayList<>();
            if (parameters != null && postprocessing != null) {
                para = parameters.stream().filter(p -> (p.getReference()).equals(entry.getKey())).collect(Collectors.toList());
                post = postprocessing.stream().filter(p -> (p.getReference()).equals(entry.getKey())).collect(Collectors.toList());
            }

            if (para.size() == 1) {
                result += para.get(0).getDisplayName() + " = " + entry.getValue() + "<br /><br />";
            } else if (post.size() == 1) {
                result += "Post: " + post.get(0).getDisplayName() + " = " + entry.getValue() + "<br /><br />";
            } else {
                result += entry.getKey() + "=" + entry.getValue() + "<br /><br />";
            }
        }

        return result.substring(0, result.length() - 12);
    }
}
