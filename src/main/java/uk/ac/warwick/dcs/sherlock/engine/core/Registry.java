package uk.ac.warwick.dcs.sherlock.engine.core;

import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotation.RequestProcessor;
import uk.ac.warwick.dcs.sherlock.api.annotation.RequestProcessor.Instance;
import uk.ac.warwick.dcs.sherlock.api.annotation.RequestProcessor.PostHandler;
import uk.ac.warwick.dcs.sherlock.api.common.IRegistry;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector.DetectorParameter;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.api.request.AbstractRequest;
import uk.ac.warwick.dcs.sherlock.api.request.RequestDatabase;
import uk.ac.warwick.dcs.sherlock.api.request.RequestDatabase.RegistryRequests;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.engine.model.ModelUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

@RequestProcessor (apiFieldName = "registry")
public class Registry implements IRegistry {

	@Instance
	static Registry instance;

	final Logger logger = LoggerFactory.getLogger(Registry.class);

	private Map<String, Class<? extends IDetector>> detectorRegistry;
	private Map<String, List<DetectorParameter>> detectorParamRegistry;

	Registry() {
		this.detectorRegistry = new ConcurrentHashMap<>();
		this.detectorParamRegistry = new ConcurrentHashMap<>();
	}

	@PostHandler
	public AbstractRequest handlePost(AbstractRequest reference) {
		if (reference instanceof RequestDatabase.RegistryRequests.GetDetectors) {
			reference.setResponse(new HashMap<>(this.detectorRegistry));
		}
		else if (reference instanceof RequestDatabase.RegistryRequests.GetDetectorNames) {
			reference.setResponse(new LinkedList<>(this.detectorRegistry.keySet()));
		}
		else if (reference instanceof RegistryRequests.GetTuneableParameters) {
			reference.setResponse(this.getTuneableParameters((String) reference.getPayload()));
		}
		return reference;
	}

	@Override
	public Boolean registerDetector(Class<? extends IDetector> detector) {

		//Do checks on detector, ensure is valid
		try {
			IDetector tester = detector.newInstance();

			for (Language lang : tester.getSupportedLanguages()) {
				Class<? extends Lexer> lexerClass = tester.getLexer(lang);
				List<IPreProcessingStrategy> preProcessingStrategies = tester.getPreProcessors();

				// Get the lexer channel list only once and store
				String[] lexerChannels = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromString("")).getChannelNames();
				for (IPreProcessingStrategy strat : preProcessingStrategies) {
					if (!ModelUtils.validatePreProcessingStrategy(strat, lexerClass.getName(), lexerChannels, tester.getParser(lang), lang)) {
						logger.warn("Detector '{}' not registered, the PreProcessingStrategy '{}' contains a preprocessor which is not valid for the {} lexer '{}' and parser '{}'", tester.getDisplayName(),
								strat.getName(), lang.name(), lexerClass.getName(), tester.getParser(lang).getName());
						return false;
					}
				}

				if (this.detectorRegistry.containsKey(tester)) {
					logger.warn("Detector '{}' not registered, registry already contains detector with same name", tester.getDisplayName());
					return false;
				}
			}

			this.detectorRegistry.put(tester.getDisplayName(), detector);

			//Do @DetectorParameter stuff - find the annotations for the params in the detector, check them and add to the map
			List<DetectorParameter> tuneables =
					Arrays.stream(detector.getDeclaredFields()).map(f -> new Tuple<>(f, f.getDeclaredAnnotationsByType(DetectorParameter.class))).filter(x -> x.getValue().length == 1).map(x -> {
						if (!(x.getKey().getType().equals(float.class) || x.getKey().getType().equals(int.class))) {
							logger.warn("Detector '{}' contains @DetectorParameter {} which is not an int or float", tester.getDisplayName(), x.getKey().getName());
							return null;
						}

						if (x.getKey().getType().equals(int.class)) {
							float[] vals = {x.getValue()[0].defaultValue(), x.getValue()[0].maxumumBound(), x.getValue()[0].minimumBound(), x.getValue()[0].step()};
							for (float f : vals) {
								if (f % 1 != 0) {
									logger.warn("Detector '{}' contains @DetectorParameter {} of type int, with a float parameter", tester.getDisplayName(), x.getKey().getName());
									return null;
								}
							}
						}

						return x;
					}).filter(Objects::nonNull).map(x -> x.getValue()[0]).collect(Collectors.toList());

			if (tuneables.size() > 0) {
				this.detectorParamRegistry.put(tester.getDisplayName(), tuneables);
			}
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return true;
	}

	IDetector getIDetectorInstance(String detectorName) {
		try {
			return this.detectorRegistry.get(detectorName).newInstance();
		}
		catch (InstantiationException | IllegalAccessException | NullPointerException e) {
			e.printStackTrace();
		}

		return null;
	}

	List<DetectorParameter> getTuneableParameters(String detectorName) {
		return this.detectorParamRegistry.getOrDefault(detectorName, null);
	}
}