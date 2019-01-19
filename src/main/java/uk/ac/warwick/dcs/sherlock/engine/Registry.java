package uk.ac.warwick.dcs.sherlock.engine;

import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.IRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.AbstractDetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector.Rank;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.*;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.engine.model.ModelUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

//@RequestProcessor (apiFieldName = "registry")
public class Registry implements IRegistry {

	//@Instance
	private static Registry instance;
	private final Logger logger = LoggerFactory.getLogger(Registry.class);

	private Map<String, LanguageData> languageRegistry;

	private Map<Class<? extends IGeneralPreProcessor>, PreProcessorData> preProcessorRegistry;
	private Map<String, AdvPreProcessorGroupData> advPreProcessorRegistry;
	private Map<Class<? extends IDetector>, DetectorData> detectorRegistry;
	private Map<Class<? extends AbstractModelTaskRawResult>, PostProcessorData> postProcRegistry;

	Registry() {
		this.languageRegistry = new ConcurrentHashMap<>();
		this.preProcessorRegistry = new ConcurrentHashMap<>();
		this.advPreProcessorRegistry = new ConcurrentHashMap<>();
		this.detectorRegistry = new ConcurrentHashMap<>();
		this.postProcRegistry = new ConcurrentHashMap<>();
	}

	/*@PostHandler
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
	}*/

	@Override
	public ITuple<Class<? extends IAdvancedPreProcessor>, Class<? extends Lexer>> getAdvancedPostProcessorForLanguage(Class<? extends IAdvancedPreProcessorGroup> group, String language) {
		if (this.advPreProcessorRegistry.containsKey(group.getName())) {
			AdvPreProcessorGroupData g = this.advPreProcessorRegistry.get(group.getName());
			if (g.preProcessors.containsKey(language.toLowerCase())) {
				AdvPreProcessorData data = g.preProcessors.get(language.toLowerCase());
				return new Tuple<>(data.clazz, data.lexer);
			}
			else {
				logger.error("Language not valid for group, this should have been validated and stopped from happening by disallowing the detector for this language");
				return null;
			}
		}
		logger.error("Group has not been registered, this should have been validated and disallowed the detector");
		return null;
	}

	@Override
	public String getDetecorDescription(Class<? extends IDetector> det) {
		if (this.detectorRegistry.containsKey(det)) {
			return this.detectorRegistry.get(det).desc;
		}

		return null;
	}

	@Override
	public List<AdjustableParameterObj> getDetectorAdjustableParameters(Class<? extends IDetector> det) {
		if (this.detectorRegistry.containsKey(det)) {
			return this.detectorRegistry.get(det).adjustables;
		}

		return null;
	}

	@Override
	public String getDetectorDisplayName(Class<? extends IDetector> det) {
		if (this.detectorRegistry.containsKey(det)) {
			return this.detectorRegistry.get(det).name;
		}

		return null;
	}

	@Override
	public Set<String> getDetectorLanguages(Class<? extends IDetector> det) {
		if (this.detectorRegistry.containsKey(det)) {
			return this.detectorRegistry.get(det).languages;
		}

		return null;
	}

	@Override
	public Rank getDetectorRank(Class<? extends IDetector> det) {
		if (this.detectorRegistry.containsKey(det)) {
			return this.detectorRegistry.get(det).rank;
		}

		return null;
	}

	@Override
	public Set<Class<? extends IDetector>> getDetectors() {
		return this.detectorRegistry.keySet();
	}

	@Override
	public Set<Class<? extends IDetector>> getDetectors(String language) {
		if (this.languageRegistry.containsKey(language)) {
			return this.languageRegistry.get(language).detectors;
		}

		return null;
	}

	@Override
	public List<AdjustableParameterObj> getPostProcessorAdjustableParameters(Class<? extends IPostProcessor> postProcessor) {
		PostProcessorData data = this.getPostProcessorData(postProcessor);
		if (data != null) {
			return data.adjustables;
		}

		return null;
	}

	@Override
	public List<AdjustableParameterObj> getPostProcessorAdjustableParametersFromDetector(Class<? extends IDetector> det) {
		if (this.detectorRegistry.containsKey(det)) {
			return this.postProcRegistry.get(this.detectorRegistry.get(det).resultClass).adjustables;
		}

		return null;
	}

	@Override
	public IPostProcessor getPostProcessorInstance(Class<? extends AbstractModelTaskRawResult> rawClass) {
		Class<? extends IPostProcessor> p = this.postProcRegistry.get(rawClass).proc;
		if (p != null) {
			try {
				return p.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				logger.error("An error occurred create IPostProcessor instance", e);
				return null;
			}
		}
		else {
			logger.warn("Could not find IPostProcessor instance to process {} object", rawClass.getName());
			return null;
		}
	}

	@Override
	public boolean registerAdvancedPreProcessorGroup(Class<? extends IAdvancedPreProcessorGroup> preProcessorGroup) {
		if (preProcessorGroup == null) {
			logger.error("Cannot register a null IAdvancedPreProcessorGroup");
			return false;
		}

		if (!this.advPreProcessorRegistry.containsKey(preProcessorGroup.getName())) {
			this.advPreProcessorRegistry.put(preProcessorGroup.getName(), new AdvPreProcessorGroupData());
			return true;
		}

		logger.info("A group for '{}' is already registered", preProcessorGroup.getName());
		return false;
	}

	@Override
	public boolean registerAdvancedPreProcessorImplementation(String groupClassPath, Class<? extends IAdvancedPreProcessor> preProcessor) {
		if (groupClassPath == null || groupClassPath.equals("")) {
			logger.error("Cannot register to a group will a blank name");
			return false;
		}

		if (preProcessor == null) {
			logger.error("Cannot register a null PreProcessor to group '{}'", groupClassPath);
			return false;
		}

		if (this.advPreProcessorRegistry.containsKey(groupClassPath)) {
			AdvPreProcessorGroupData group = this.advPreProcessorRegistry.get(groupClassPath);

			Class<?> type = Arrays.stream(preProcessor.getDeclaredMethods()).filter(x -> x.getName().equals("process")).map(x -> x.getParameterTypes()[0]).findAny().orElse(null);

			if (type == null) {
				logger.error("Could not verify the generic type for the IAdvancedPreProcessor '{}' is correct, not registering", preProcessor.getName());
				return false;
			}

			AdvPreProcessorData data = new AdvPreProcessorData();
			data.lexer = (Class<? extends Lexer>) type;
			data.clazz = preProcessor;

			this.languageRegistry.forEach((k, v) -> {
				if (v.lexers.contains(data.lexer)) {
					if (!group.preProcessors.containsKey(k)) {
						group.preProcessors.put(k, data);
					}
					else {
						logger.warn("IAdvancedPreProcessorGroup '{}' already contains a PreProcessor for language '{}', not registering '{}' for this language even though it is valid", groupClassPath,
								v.dispName, preProcessor.getName());
					}
				}
			});

			return true;
		}

		logger.info("Group at classpath '{}' does not exist", groupClassPath);
		return false;
	}

	@Override
	public boolean registerDetector(Class<? extends IDetector> detector) {

		if (detector == null) {
			logger.error("Passed null detector");
			return false;
		}

		IDetector tester;
		try {
			tester = detector.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			logger.error("Ensure IDetector '{}' has a nullary constructor", detector.getName());
			return false;
		}
		catch (NoClassDefFoundError e) {
			logger.warn("IDetector '{}' not registered. Could not find the required class dependency '{}'", detector.getName(), e.getMessage());
			return false;
		}

		// Check generics for detector
		Class<? extends AbstractDetectorWorker> workerClass;
		try {
			workerClass = (Class<? extends AbstractDetectorWorker>) getGenericClass(detector.getGenericSuperclass());
		}
		catch (ClassCastException | ClassNotFoundException | NullPointerException e) {
			logger.error("IDetector '{}' not registered. It has no AbstractDetectorWorker type (its generic parameter), this is not allowed. A generic type MUST be given", detector.getName());
			return false;
		}

		// Check generics for detector workers
		Class<? extends AbstractModelTaskRawResult> resultsClass;
		try {
			resultsClass = (Class<? extends AbstractModelTaskRawResult>) getGenericClass(workerClass.getGenericSuperclass());
		}
		catch (ClassCastException | ClassNotFoundException | NullPointerException e) {
			logger.error(
					"IDetector '{}' not registered. AbstractDetectorWorker '{}' has no AbstractModelTaskRawResults type (its generic parameter), this is not allowed. A generic type MUST be given",
					detector.getName(), workerClass.getName());
			return false;
		}

		//Do checks on detector, ensure is valid
		try {
			for (Language lang : tester.getSupportedLanguages()) {
				Class<? extends Lexer> lexerClass = tester.getLexer(lang);
				List<IPreProcessingStrategy> preProcessingStrategies = tester.getPreProcessors();

				// Get the lexer channel list only once and store
				String[] lexerChannels = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromString("")).getChannelNames();
				for (IPreProcessingStrategy strat : preProcessingStrategies) {
					if (!ModelUtils.validatePreProcessingStrategy(strat, lexerClass.getName(), lexerChannels, tester.getParser(lang), lang)) {
						logger.warn("Detector '{}' not registered, the PreProcessingStrategy '{}' contains a preprocessor which is not valid for the {} lexer '{}' and parser '{}'",
								tester.getDisplayName(), strat.getName(), lang.name(), lexerClass.getName(), tester.getParser(lang).getName());
						return false;
					}
				}

				if (this.detectorRegistry.containsKey(tester)) {
					logger.warn("Detector '{}' not registered, registry already contains detector with same name", tester.getDisplayName());
					return false;
				}
			}

			DetectorData data = new DetectorData();
			data.name = tester.getDisplayName();
			data.desc = "NOT YET IMPLEMENTED, SORRY";
			data.rank = tester.getRank();
			data.strategies = tester.getPreProcessors();
			data.resultClass = resultsClass;
			this.detectorRegistry.put(detector, data);

			//Do @DetectorParameter stuff - find the annotations for the params in the detector, check them and add to the map
			List<AdjustableParameterObj> tuneables =
					Arrays.stream(detector.getDeclaredFields()).map(f -> new Tuple<>(f, f.getDeclaredAnnotationsByType(AdjustableParameter.class))).filter(x -> x.getValue().length == 1).map(x -> {
						if (!(x.getKey().getType().equals(float.class) || x.getKey().getType().equals(int.class))) {
							logger.warn("Detector '{}' not registered, contains @DetectorParameter {} which is not an int or float", tester.getDisplayName(), x.getKey().getName());
							return null;
						}

						if (x.getKey().getType().equals(int.class)) {
							float[] vals = { x.getValue()[0].defaultValue(), x.getValue()[0].maxumumBound(), x.getValue()[0].minimumBound(), x.getValue()[0].step() };
							for (float f : vals) {
								if (f % 1 != 0) {
									logger.warn("Detector '{}' not registered, contains @DetectorParameter {} of type int, with a float parameter", tester.getDisplayName(), x.getKey().getName());
									return null;
								}
							}
						}

						return x;
					}).filter(Objects::nonNull).map(x -> new AdjustableParameterObj(x.getValue()[0], x.getKey(), true)).collect(Collectors.toList());

			if (tuneables.size() > 0) {
				data.adjustables = tuneables;
			}
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean registerGeneralPreProcessor(Class<? extends IGeneralPreProcessor> preProcessor) {

		PreProcessorData data = new PreProcessorData();

		try {
			ILexerSpecification spec = preProcessor.newInstance().getLexerSpecification();
			this.languageRegistry.forEach((k, v) -> {
				for (Class<? extends Lexer> lex : v.lexers) {
					try {
						Field field = lex.getDeclaredField("channelNames");
						field.setAccessible(true);
						if (ModelUtils.checkLexerAgainstSpecification((String[]) field.get(new String[] {}), spec)) {
							data.langLexerRef.put(k, lex);
							break;
						}
					}
					catch (IllegalAccessException | NoClassDefFoundError | NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
			});
		}
		catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (NoClassDefFoundError e) {
			logger.warn("IGeneralPreProcessor '{}' not registered. Could not find the required class dependency '{}'", preProcessor.getName(), e.getMessage());
			return false;
		}

		this.preProcessorRegistry.put(preProcessor, data);
		return false;
	}

	@Override
	public boolean registerLanguage(String name, Class<? extends Lexer> lexer) {
		if (name != null && !name.equals("") && lexer != null) {
			if (name.length() > 32) {
				logger.error("Cannot register language '{}', name is too long");
			}

			LanguageData data;
			if (this.languageRegistry.containsKey(name.toLowerCase())) {
				data = this.languageRegistry.get(name.toLowerCase());
			}
			else {
				data = new LanguageData(name);
				this.languageRegistry.put(name.toLowerCase(), data);
			}

			data.lexers.add(lexer);
			return true;
		}

		logger.error("Cannot register a language with a blank name or lexer");
		return false;
	}

	@Override
	public final boolean registerPostProcessor(Class<? extends IPostProcessor> postProcessor, Class<? extends AbstractModelTaskRawResult> handledResultType) {

		if (postProcessor == null || handledResultType == null) {
			logger.error("PostProcessor and/or AbstractModelTaskRawResult classes cannot be null");
		}

		try {
			postProcessor.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			logger.error("Ensure IPostProcessor '{}'has a nullary constructors", postProcessor.getName());
			return false;
		}
		catch (NoClassDefFoundError e) {
			logger.warn("IPostProcessor '{}' not registered. Could not find the required class dependency '{}'", postProcessor.getName(), e.getMessage());
			return false;
		}

		if (this.postProcRegistry.containsKey(handledResultType)) {
			logger.error("RawResult class '{}' already mapped to a postprocessor", handledResultType.getName());
			return false;
		}

		if (this.getPostProcessorData(postProcessor) != null) {
			logger.error("IPostProcessor '{}' already mapped to a RawResult class", postProcessor.getName());
			return false;
		}

		PostProcessorData data = new PostProcessorData();

		if (postProcessor != null && handledResultType != null) {
			//get the type T of the postprocessor
			try {
				ParameterizedType type =
						Arrays.stream(postProcessor.getDeclaredMethods()).filter(x -> x.getName().equals("processResults")).map(x -> (ParameterizedType) x.getGenericParameterTypes()[1]).findAny()
								.orElse(null);

				if (type == null) {
					logger.error("Could not verify the generic type for the IPostProcessor '{}' is correct", postProcessor.getName());
					return false;
				}

				if (handledResultType.getName().equals(type.getActualTypeArguments()[0].getTypeName())) {
					data.proc = postProcessor;
				}
				else {
					logger.error("Generic type of the IPostProcessor '{}', does not match the type it has been registered with ('{}')", postProcessor.getName(), handledResultType.getName());
					return false;
				}
			}
			catch (ClassCastException e) {
				logger.error("IPostProcessor has no raw result type (its generic parameter), this is not allowed. A generic type MUST be given");
				return false;
			}
		}
		else {
			logger.warn("Bad IPostProcessor registration");
			return false;
		}

		// Do @DetectorParameter stuff - find the annotations for the params in the postprocessor, check them and add to the map
		List<AdjustableParameterObj> tuneables =
				Arrays.stream(postProcessor.getDeclaredFields()).map(f -> new Tuple<>(f, f.getDeclaredAnnotationsByType(AdjustableParameter.class))).filter(x -> x.getValue().length == 1).map(x -> {
					if (!(x.getKey().getType().equals(float.class) || x.getKey().getType().equals(int.class))) {
						logger.warn("PostProcessor '{}' not registered, contains @DetectorParameter {} which is not an int or float", postProcessor.getName(), x.getKey().getName());
						return null;
					}

					if (x.getKey().getType().equals(int.class)) {
						float[] vals = { x.getValue()[0].defaultValue(), x.getValue()[0].maxumumBound(), x.getValue()[0].minimumBound(), x.getValue()[0].step() };
						for (float f : vals) {
							if (f % 1 != 0) {
								logger.warn("PostProcessor '{}' not registered, contains @DetectorParameter {} of type int, with a float parameter", postProcessor.getName(), x.getKey().getName());
								return null;
							}
						}
					}

					return x;
				}).filter(Objects::nonNull).map(x -> new AdjustableParameterObj(x.getValue()[0], x.getKey(), false)).collect(Collectors.toList());

		if (tuneables.size() > 0) {
			data.adjustables = tuneables;
		}

		this.postProcRegistry.put(handledResultType, data);
		return true;
	}

	void analyseDetectors() {
		this.detectorRegistry.forEach((k,v) -> {
			Set<String> supportedLanguages = new HashSet<>();

			for (IPreProcessingStrategy strat : v.strategies) {
				if (strat.isAdvanced()){
					this.insersectSets(supportedLanguages, this.advPreProcessorRegistry.get(strat.getPreProcessorClasses().get(0).getName()).preProcessors.keySet());
				}
				else {
					strat.getPreProcessorClasses().forEach(x -> {
						if (this.preProcessorRegistry.containsKey(x)) {
							this.insersectSets(supportedLanguages, this.preProcessorRegistry.get(x).langLexerRef.keySet());
						}
					});
				}
			}

			v.languages = supportedLanguages;
			supportedLanguages.forEach(l -> this.languageRegistry.get(l).detectors.add(k));
			//logger.error(k.getName() + " - " + supportedLanguages.toString());
		});
	}

	private void insersectSets(Set master, Set s2) {
		if (master.size() == 0) {
			master.addAll(s2);
		}
		else {
			master.retainAll(s2);
		}
	}

	private Class<?> getGenericClass(Type genericSuperclass) throws ClassNotFoundException {
		ParameterizedType type = this.getHighestParamType(genericSuperclass);
		String typeName = type.getActualTypeArguments()[0].getTypeName().split("<")[0];
		return Class.forName(typeName);
	}

	private ParameterizedType getHighestParamType(Type type) {
		while (!(type instanceof ParameterizedType)) {
			try {
				type = ((Class<?>) type).getGenericSuperclass();
			}
			catch (NullPointerException e) {
				return null;
			}
		}
		return (ParameterizedType) type;
	}

	private PostProcessorData getPostProcessorData(Class<? extends IPostProcessor> postProcessor) {
		return this.postProcRegistry.values().stream().filter(x -> x.proc.equals(postProcessor)).findFirst().orElse(null);
	}

	private class DetectorData {

		String name;
		String desc;
		Rank rank;
		Set<String> languages;
		List<IPreProcessingStrategy> strategies;
		List<AdjustableParameterObj> adjustables;
		Class<? extends AbstractModelTaskRawResult> resultClass;

	}

	private class PostProcessorData {

		Class<? extends IPostProcessor> proc;
		List<AdjustableParameterObj> adjustables;
	}

	private class PreProcessorData {

		Map<String, Class<? extends Lexer>> langLexerRef;

		PreProcessorData() {
			this.langLexerRef = new HashMap<>();
		}
	}

	private class AdvPreProcessorGroupData {

		Map<String, AdvPreProcessorData> preProcessors;

		AdvPreProcessorGroupData() {
			this.preProcessors = new HashMap<>();
		}

	}

	private class AdvPreProcessorData {

		Class<? extends IAdvancedPreProcessor> clazz;
		Class<? extends Lexer> lexer;

		public Class<? extends IAdvancedPreProcessor> getClazz() {
			return clazz;
		}

		public Class<? extends Lexer> getLexer() {
			return lexer;
		}
	}

	private class LanguageData {

		String dispName;
		Set<Class<? extends Lexer>> lexers;
		Set<Class<? extends IDetector>> detectors;

		LanguageData(String dispName) {
			this.dispName = dispName;
			this.lexers = new HashSet<>();
			this.detectors = new HashSet<>();
		}
	}
}
