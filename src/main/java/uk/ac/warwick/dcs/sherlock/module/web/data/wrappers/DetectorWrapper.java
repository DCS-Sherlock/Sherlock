package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.DetectorNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.ParameterNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TParameter;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.ParameterForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.TParameterRepository;

import java.util.*;

/**
 * The wrapper that manages database detectors
 */
public class DetectorWrapper {
    /**
     * The detector to manage
     */
    private TDetector tDetector;

    /**
     * Whether or not the current account owns the detector
     */
    private boolean isOwner = false;

    /**
     * Empty initialiser
     */
    public DetectorWrapper() {}

    /**
     * Initialise the wrapper with a template that has already been loaded
     *
     * @param tDetector the detector to manage
     * @param isOwner whether the account owns the detector
     */
    public DetectorWrapper(TDetector tDetector, boolean isOwner) {
        this.tDetector = tDetector;
        this.isOwner = isOwner;
    }

    /**
     * Initialise the wrapper by trying to find a detector that is
     * either owned by the account or is public
     *
     * @param id the template id to find
     * @param account the account of the current user
     * @param tDetectorRepository the detector database repository
     *
     * @throws DetectorNotFound if the detector wasn't found
     */
    public DetectorWrapper(
            long id,
            Account account,
            TDetectorRepository tDetectorRepository
    ) throws DetectorNotFound {
        Optional<TDetector> optional = tDetectorRepository.findById(id);

        if (!optional.isPresent()) {
            throw new DetectorNotFound("Detector not found.");
        }

        this.tDetector = optional.get();

        TemplateWrapper templateWrapper = new TemplateWrapper(this.tDetector.getTemplate(), account);

        if (templateWrapper.isOwner() == false && templateWrapper.getTemplate().isPublic() == false)
            throw new DetectorNotFound("Detector not found.");

        this.isOwner = templateWrapper.isOwner();
    }

    /**
     * Get the detector
     * @return the detector
     */
    public TDetector getDetector() {
        return tDetector;
    }

    /**
     * Set the detector
     *
     * @param tDetector the new detector
     */
    public void setDetector(TDetector tDetector) {
        this.tDetector = tDetector;
    }

    /**
     * Get the list of adjustable parameters for this detector
     *
     * @return the list of adjustable parameters
     *
     * @throws DetectorNotFound if the detector no longer exists
     */
    public List<AdjustableParameterObj> getEngineParameters() throws DetectorNotFound {
        List<AdjustableParameterObj> result = new ArrayList<>();

        List<AdjustableParameterObj> detector = SherlockRegistry.getDetectorAdjustableParameters(this.getEngineDetector());
        if (detector != null) {
            result.addAll(detector);
        }

        return result;
    }

    /**
     * Get the list of adjustable postprocessing parameters for this detector
     *
     * @return the list of adjustable postprocessing parameters
     *
     * @throws DetectorNotFound if the detector no longer exists
     */
    public List<AdjustableParameterObj> getEnginePostProcessingParameters() throws DetectorNotFound {
        List<AdjustableParameterObj> result = new ArrayList<>();

        List<AdjustableParameterObj> post = SherlockRegistry.getPostProcessorAdjustableParametersFromDetector(this.getEngineDetector());
        if (post != null) {
            result.addAll(post);
        }

        return result;
    }

    /**
     * Get the adjustable parameters for this detector as a map
     *
     * @return the map of adjustable parameters
     *
     * @throws DetectorNotFound if the detector no longer exists
     */
    public Map<String, AdjustableParameterObj> getEngineParametersMap() throws DetectorNotFound {
        Map<String, AdjustableParameterObj> map = new HashMap<>();
        for (AdjustableParameterObj p : this.getEngineParameters()) {
            map.put(p.getName(), p);
        }
        return map;
    }

    public Map<String, AdjustableParameterObj> getEnginePostProcessingParametersMap() throws DetectorNotFound {
        Map<String, AdjustableParameterObj> map = new HashMap<>();
        for (AdjustableParameterObj p : this.getEnginePostProcessingParameters()) {
            map.put(p.getName(), p);
        }
        return map;
    }

    /**
     * Get the id of the detector
     *
     * @return the id
     */
    public long getId() {
        return this.tDetector.getId();
    }

    /**
     * Get the engine object for this detector
     *
     * @return the IDetector
     *
     * @throws DetectorNotFound if the engine detector no longer exists
     */
    public Class<? extends IDetector> getEngineDetector() throws DetectorNotFound {
        Class<? extends IDetector> detector = null;
        try {
            detector = (Class<? extends IDetector>) Class.forName(this.tDetector.getName(), true, SherlockEngine.classloader);
        } catch (ClassNotFoundException e) {
            throw new DetectorNotFound("Detector no longer exists");
        }
        return detector;
    }

    /**
     * Get the engine wrapper for this detector
     *
     * @return the engine detector wrapper
     *
     * @throws DetectorNotFound if the engine detector no longer exists
     */
    public EngineDetectorWrapper getWrapper() throws DetectorNotFound {
        Class<? extends IDetector> detector = this.getEngineDetector();

        if (detector == null) {
            return null;
        }

        return new EngineDetectorWrapper(detector);
    }

    /**
     * Get the list of parameters for this detector
     *
     * @return the list of parameters
     *
     * @throws ParameterNotFound
     * @throws DetectorNotFound if the engine detector no longer exists
     */
    public List<ParameterWrapper> getParametersList() throws DetectorNotFound, ParameterNotFound {
        List<ParameterWrapper> list = new ArrayList<>();

        for (TParameter p : this.tDetector.getParameters()) {
            if (p.isPostprocessing()) {
                list.add(new ParameterWrapper(p, this.getEnginePostProcessingParametersMap()));
            } else {
                list.add(new ParameterWrapper(p, this.getEngineParametersMap()));
            }
        }

        return list;
    }

    /**
     * Update the parameters for this detector
     *
     * @param parameterForm the form to use
     * @param tParameterRepository the database repository
     */
    public void updateParameters(ParameterForm parameterForm, TParameterRepository tParameterRepository) {
        List<TParameter> currentParameters = tParameterRepository.findByTDetector(this.tDetector);
        tParameterRepository.deleteAll(currentParameters);

        for (Map.Entry<String, Float> entry : parameterForm.getParameters().entrySet()) {
            TParameter parameter = new TParameter(entry.getKey(), entry.getValue(), false, this.tDetector);
            tParameterRepository.save(parameter);
        }

        for (Map.Entry<String, Float> entry : parameterForm.getPostprocessing().entrySet()) {
            TParameter postparameter = new TParameter(entry.getKey(), entry.getValue(), true, this.tDetector);
            tParameterRepository.save(postparameter);
        }
    }
}
