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

public class DetectorWrapper {
    private TDetector tDetector;
    private boolean isOwner = false;

    public DetectorWrapper() {}

    public DetectorWrapper(TDetector tDetector, boolean isOwner) {
        this.tDetector = tDetector;
        this.isOwner = isOwner;
    }

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

    public TDetector getDetector() {
        return tDetector;
    }

    public void setDetector(TDetector tDetector) {
        this.tDetector = tDetector;
    }

    public List<AdjustableParameterObj> getEngineParameters() throws DetectorNotFound {
        return SherlockRegistry.getDetectorAdjustableParameters(this.getEngineDetector());
    }

    public Map<String, AdjustableParameterObj> getEngineParametersMap() throws DetectorNotFound {
		Map<String, AdjustableParameterObj> map = new HashMap<>();
		for (AdjustableParameterObj p : this.getEngineParameters()) {
			map.put(p.getName(), p);
		}
		return map;
    }

    public long getId() {
        return this.tDetector.getId();
    }

    public Class<? extends IDetector> getEngineDetector() throws DetectorNotFound {
        Class<? extends IDetector> detector = null;
        try {
            detector = (Class<? extends IDetector>) Class.forName(this.tDetector.getName(), true, SherlockEngine.classloader);
        } catch (ClassNotFoundException e) {
            throw new DetectorNotFound("Detector no longer exists");
        }
        return detector;
    }

    public EngineDetectorWrapper getWrapper() throws DetectorNotFound {
        Class<? extends IDetector> detector = this.getEngineDetector();

        if (detector == null) {
            return null;
        }

        return new EngineDetectorWrapper(detector);
    }

    public List<ParameterWrapper> getParametersList() throws ParameterNotFound, DetectorNotFound {
        List<ParameterWrapper> list = new ArrayList<>();

        for (TParameter p : this.tDetector.getParameters()) {
            list.add(new ParameterWrapper(p, this.getEngineParametersMap()));
        }

        return list;
    }

    public void updateParameters(ParameterForm parameterForm, TParameterRepository tParameterRepository) {
        List<TParameter> currentParameters = tParameterRepository.findByTDetector(this.tDetector);
        tParameterRepository.deleteAll(currentParameters);

        for (Map.Entry<String, Float> entry : parameterForm.parameters.entrySet()) {
            TParameter tParameter = new TParameter(entry.getKey(), entry.getValue(), this.tDetector);
            tParameterRepository.save(tParameter);
        }

    }
}
