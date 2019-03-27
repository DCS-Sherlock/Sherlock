package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The wrapper that manages engine detectors
 */
public class EngineDetectorWrapper {
    /**
     * The detector display name
     */
    private String displayName;

    /**
     * The detector class name
     */
    private String className;

    /**
     * The set of languages supported by the detector
     */
    private Set<String> languages;

    /**
     * The detector description
     */
    private String description;

    /**
     * Initialise the wrapper class using the detector entity
     *
     * @param detector the detector entity
     */
    public EngineDetectorWrapper(Class<? extends IDetector> detector) {
        this.className = detector.getName();
        this.displayName = SherlockRegistry.getDetectorDisplayName(detector);
        this.languages = SherlockRegistry.getDetectorLanguages(detector);
        this.description = SherlockRegistry.getDetectorDescription(detector);
    }

    /**
     * Get the detector display name
     *
     * @return the name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the detector display name
     *
     * @param displayName the new name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the detector class name
     *
     * @return the name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Set the detector class name
     *
     * @param className the new name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Get the set of languages
     *
     * @return the languages
     */
    public Set<String> getLanguages() {
        return languages;
    }

    /**
     * Set the languages
     *
     * @param languages the new languages
     */
    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    /**
     * Get the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the list of detector wrappers for a language
     *
     * @param language the language to filter by
     *
     * @return the list of wrappers
     */
    public static List<EngineDetectorWrapper> getDetectors(String language) {
        List<EngineDetectorWrapper> list = new ArrayList<>();
        if (SherlockRegistry.getLanguages().contains(language)) {
            SherlockRegistry.getDetectors(language).forEach(d -> list.add(new EngineDetectorWrapper(d)));
        }
        return list;
    }

    /**
     * Get the list of detector names for a language
     *
     * @param language the language to filter by
     *
     * @return the list of names
     */
    public static List<String> getDetectorNames(String language) {
        List<String> list = new ArrayList<>();
        if (SherlockRegistry.getLanguages().contains(language)) {
            SherlockRegistry.getDetectors(language).forEach(d -> list.add(d.getName()));
        }
        return list;
    }
}
