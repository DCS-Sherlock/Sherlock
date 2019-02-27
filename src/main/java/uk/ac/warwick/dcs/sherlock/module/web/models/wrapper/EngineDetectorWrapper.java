package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EngineDetectorWrapper {
    private String displayName;
    private String className;
    private Set<String> languages;
    private String description;

    public EngineDetectorWrapper() { }

    public EngineDetectorWrapper(Class<? extends IDetector> detector) {
        this.className = detector.getName();
        this.displayName = SherlockRegistry.getDetectorDisplayName(detector);
        this.languages = SherlockRegistry.getDetectorLanguages(detector);
        this.description = SherlockRegistry.getDetectorDescription(detector);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static List<EngineDetectorWrapper> getDetectors(String language) {
        List<EngineDetectorWrapper> list = new ArrayList<>();
        if (SherlockRegistry.getLanguages().contains(language)) {
            SherlockRegistry.getDetectors(language).forEach(d -> list.add(new EngineDetectorWrapper(d)));
        }
        return list;
    }

    public static List<String> getDetectorNames(String language) {
        List<String> list = new ArrayList<>();
        if (SherlockRegistry.getLanguages().contains(language)) {
            SherlockRegistry.getDetectors(language).forEach(d -> list.add(d.getName()));
        }
        return list;
    }
}
