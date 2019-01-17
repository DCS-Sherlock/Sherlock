package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.DetectorNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.TParameter;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.ParameterForm;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TDetectorRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.TParameterRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        if (optional.isEmpty()) {
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

    public List<AdjustableParameterObj> getEngineParameters() {
        return SherlockRegistry.getDetectorAdjustableParameters(this.getEngineDetector());
    }

    public Map<String, AdjustableParameterObj> getEngineParametersMap() {
		Map<String, AdjustableParameterObj> map = new HashMap<>();
		for (AdjustableParameterObj p : this.getEngineParameters()) {
			map.put(p.getName(), p);
		}
		return map;
    }

    public long getId() {
        return this.tDetector.getId();
    }

    public Class<? extends IDetector> getEngineDetector() {
        Class<? extends IDetector> detector = null;
        try {
            detector = (Class<? extends IDetector>) Class.forName(this.tDetector.getName());
        } catch (ClassNotFoundException e) {
            //TODO: deal with error
            e.printStackTrace();
        }
        return detector;
    }

    public EngineDetectorWrapper getWrapper() {
        Class<? extends IDetector> detector = null;
        try {
            detector = (Class<? extends IDetector>) Class.forName(this.tDetector.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); //TODO: deal with error
        }
        if (detector == null) {
            return null;
        }

        return new EngineDetectorWrapper(detector);
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
