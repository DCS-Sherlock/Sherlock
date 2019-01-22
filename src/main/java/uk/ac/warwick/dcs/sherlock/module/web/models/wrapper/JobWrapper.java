package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JobWrapper {
    private IJob job;
    private List<TaskWrapper> tasks;

    public JobWrapper(IJob job) {
        this.job = job;
        this.tasks = new ArrayList<>();
        this.job.getTasks().forEach(t -> this.tasks.add(new TaskWrapper(t)));
    }

    public long getPersistentId() {
        return this.job.getPersistentId();
    }

    public String getStatus() {
        return this.job.getStatus().toString();
    }

    public String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        return this.job.getTimestamp().format(formatter);
    }

    public int getNumSubmissions() {
        return this.job.getFiles().length;
    }

    public List<TaskWrapper> getTasks() {
        return this.tasks;
    }
}
