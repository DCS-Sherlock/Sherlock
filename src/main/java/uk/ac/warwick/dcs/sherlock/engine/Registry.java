package uk.ac.warwick.dcs.sherlock.engine;

import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotations.RequestProcessor;
import uk.ac.warwick.dcs.sherlock.api.common.IRegistry;
import uk.ac.warwick.dcs.sherlock.api.common.Request;
import uk.ac.warwick.dcs.sherlock.api.common.RequestDatabase;
import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.engine.model.ModelUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;

@RequestProcessor (apiFieldName = "registry")
class Registry implements IRegistry {

	@RequestProcessor.Instance
	static Registry instance;

	final Logger logger = LoggerFactory.getLogger(Registry.class);
	private Map<String, Class<? extends IDetector>> detectorRegistry;

	Registry() {
		this.detectorRegistry = new ConcurrentHashMap<>();
	}

	@RequestProcessor.PostHandler
	public Request handlePost(Request reference) {
		if (reference instanceof RequestDatabase.RegistryRequests.GetDetectors) {
			reference.setResponce(new HashMap<>(this.detectorRegistry));
		}
		else if (reference instanceof RequestDatabase.RegistryRequests.GetDetectorNames) {
			reference.setResponce(new LinkedList<>(this.detectorRegistry.keySet()));
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
					if (!ModelUtils.validatePreProcessingStrategy(strat, lexerClass.getName(), lexerChannels)) {
						logger.warn("Detector {} not registered, the PreProcessingStrategy '{}' contains a preprocessor which is not valid for the {} lexer '{}'", tester.getDisplayName(),
								strat.getName(), lang.name(), lexerClass.getName());
						return false;
					}
				}

				if (this.detectorRegistry.containsKey(tester)) {
					logger.warn("Detector {} not registered, registry already contains detector with same name", tester.getDisplayName());
					return false;
				}

				this.detectorRegistry.put(tester.getDisplayName(), detector);
				return true;
			}
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return false;
	}
}
