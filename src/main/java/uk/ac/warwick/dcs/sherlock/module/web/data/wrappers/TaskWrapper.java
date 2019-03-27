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

        for (Map.Entry<String, Float> entry : task.getParameterMapping().entrySet()) {
            List<AdjustableParameterObj> temp = parameters.stream().filter(p -> (task.getDetector().getName() + ":" + p.getName()).equals(entry.getKey())).collect(Collectors.toList());

            if (temp.size() == 1) {
                result += temp.get(0).getDisplayName() + " = " + entry.getValue() + "<br /><br />";
            } else {
                result += entry.getKey() + "=" + entry.getValue() + "<br /><br />";
            }
        }

        return result.substring(0, result.length() - 12);
    }
}
