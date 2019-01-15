package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import java.util.ArrayList;
import java.util.List;

public class EngineDetectorWrapper {
    private String displayName;
    private String className;
    private Language[] languages;
    private String description;

    public EngineDetectorWrapper() { }

    public EngineDetectorWrapper(Class<? extends IDetector> detector) {
        this.className = detector.getName();

//        try {
//            IDetector tester = detector.newInstance();
//            this.displayName = tester.getDisplayName();
//            this.languages = tester.getSupportedLanguages();
////            this.description = tester.getDescription();
//            this.description = "NOT IMPLEMENTED YET";
//        } catch (InstantiationException | IllegalAccessException e) {
//            e.printStackTrace(); //TODO: catch error properly
//        }

        this.displayName = SherlockRegistry.getDetectorDisplayName(detector);
        this.languages = SherlockRegistry.getDetectorLanguages(detector);
        this.description = SherlockRegistry.getDetecorDescription(detector);

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

    public Language[] getLanguages() {
        return languages;
    }

    public void setLanguages(Language[] languages) {
        this.languages = languages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static List<EngineDetectorWrapper> getDetectors(Language language) {
        List<EngineDetectorWrapper> list = new ArrayList<>();
        SherlockRegistry.getDetectors(language).forEach(d -> list.add(new EngineDetectorWrapper(d)));
        return list;
    }

    public static List<String> getDetectorNames(Language language) {
        List<String> list = new ArrayList<>();
        SherlockRegistry.getDetectors(language).forEach(d -> list.add(d.getName()));
        return list;
    }
}
