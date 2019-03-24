package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import uk.ac.warwick.dcs.sherlock.engine.component.ITask;

import java.util.Map;

public class TaskWrapper {
    private ITask task;
    private EngineDetectorWrapper detectorWrapper;

    public TaskWrapper(ITask task) {
        this.task = task;
        this.detectorWrapper = new EngineDetectorWrapper(this.task.getDetector());
    }

    public String getDisplayName() {
        return this.detectorWrapper.getDisplayName();
    }

    public String getParameterString() {
        String result = "";

        for (Map.Entry<String, Float> entry : task.getParameterMapping().entrySet()) {
            result += entry.getKey() + "=" + entry.getValue() + "<br /><br />";
        }

        return result.substring(0, result.length() - 12);
    }
}
