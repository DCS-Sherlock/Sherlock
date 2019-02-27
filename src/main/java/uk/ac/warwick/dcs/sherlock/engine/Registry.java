package uk.ac.warwick.dcs.sherlock.engine;

import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import uk.ac.warwick.dcs.sherlock.api.IRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.AbstractDetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectorRank;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.*;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Registry implements IRegistry {

	private final Logger logger = LoggerFactory.getLogger(Registry.class);

	private Map<String, LanguageData> languageRegistry;
	private Map<String, DetectionType> detectionTypeRegistry;

	private Map<Class<? extends IGeneralPreProcessor>, PreProcessorData> preProcessorRegistry;
	private Map<String, AdvPreProcessorGroupData> advPreProcessorRegistry;
	private Map<Class<? extends IDetector>, DetectorData> detectorRegistry;
	private Map<Class<? extends AbstractModelTaskRawResult>, PostProcessorData> postProcRegistry;

	private Map<PreProcessingStrategy, Map<String, Class<? extends Lexer>>> strategyLexerCache;

	Registry() {
		this.languageRegistry = new ConcurrentHashMap<>();
		this.detectionTypeRegistry = new ConcurrentHashMap<>();

		this.preProcessorRegistry = new ConcurrentHashMap<>();
		this.advPreProcessorRegistry = new ConcurrentHashMap<>();
		this.detectorRegistry = new ConcurrentHashMap<>();
		this.postProcRegistry = new ConcurrentHashMap<>();

		this.strategyLexerCache = new ConcurrentHashMap<>();
	}

	/**
	 * Fetches the top level generic type
	 *
	 * @param genericSuperclass class.getGenericSuperclass()
	 *
	 * @return The generic type class if found
	 *
	 * @throws ClassNotFoundException if could not find a superclass with a static generic type
	 */
	private static Class<?> getGenericClass(Type genericSuperclass) throws ClassNotFoundException {
		ParameterizedType type = getHighestParamType(genericSuperclass);
		String typeName = type.getActualTypeArguments()[0].getTypeName().split("<")[0];
		return Class.forName(typeName, true, SherlockEngine.classloader);
	}

	private static ParameterizedType getHighestParamType(Type type) {
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
	public String getDetectorDescription(Class<? extends IDetector> det) {
		if (this.detectorRegistry.containsKey(det)) {
			return this.detectorRegistry.get(det).desc;
		}

		return null;
	}

	@Override
	public DetectionType getDetectionType(String identifier) throws UnknownDetectionTypeException {
		if (this.detectionTypeRegistry.containsKey(identifier)) {
			return this.detectionTypeRegistry.get(identifier);
		}
		else {
			throw new UnknownDetectionTypeException("Detection Type '%s' is not recognised, verify that all required modules are present and active");
		}
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
	public DetectorRank getDetectorRank(Class<? extends IDetector> det) {
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
		if (this.languageRegistry.containsKey(language.toLowerCase())) {
			return this.languageRegistry.get(language.toLowerCase()).detectors;
		}

		return null;
	}

	@Override
	public Set<String> getLanguages() {
		return this.languageRegistry.values().stream().map(v -> v.dispName).collect(Collectors.toSet());
	}

	@Override
	public Class<? extends Lexer> getLexerForStrategy(PreProcessingStrategy strategy, String language) {
		if (strategy == null) {
			logger.error("Strategy cannot be null");
			return null;
		}

		if (language == null || language.equals("")) {
			logger.error("Language cannot be null");
			return null;
		}

		if (!this.languageRegistry.containsKey(language.toLowerCase())) {
			logger.error("Language '{}' is has not been registered");
			return null;
		}

		if (strategy.isAdvanced()) {
			return this.advPreProcessorRegistry.get(strategy.getPreProcessorClasses().get(0).getName()).preProcessors.getOrDefault(language.toLowerCase(), null).lexer;
		}
		else {
			if (!this.strategyLexerCache.containsKey(strategy)) {
				this.strategyLexerCache.put(strategy, new HashMap<>());
			}

			if (this.strategyLexerCache.get(strategy).containsKey(language.toLowerCase())) {
				return this.strategyLexerCache.get(strategy).get(language.toLowerCase());
			}
			else {
				Set<Class<? extends Lexer>> lexers = this.languageRegistry.get(language.toLowerCase()).lexers;

				for (Class<? extends Lexer> lexer : lexers) {
					boolean inAll = strategy.getPreProcessorClasses().stream().allMatch(s -> {
						PreProcessorData data = this.preProcessorRegistry.get(s);
						if (data.langLexerRef.containsKey(language.toLowerCase())) {
							return data.langLexerRef.get(language.toLowerCase()).contains(lexer);
						}
						else {
							return false;
						}
					});

					if (inAll) {
						this.strategyLexerCache.get(strategy).put(language.toLowerCase(), lexer);
						return lexer;
					}
				}

				return null;
			}
		}
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
			e.printStackTrace();
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

		//Do checks on actuall detector, ensure is valid
		if (this.detectorRegistry.containsKey(tester)) {
			logger.warn("Detector '{}' not registered, registry already contains detector with same name", tester.getDisplayName());
			return false;
		}

		DetectorData data = new DetectorData();
		data.name = tester.getDisplayName();
		data.desc = "NOT YET IMPLEMENTED, SORRY";
		data.rank = tester.getRank();
		data.strategies = tester.getPreProcessors();
		data.resultClass = resultsClass;

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

		this.detectorRegistry.put(detector, data);
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
						if (this.checkLexerAgainstSpecification((String[]) field.get(new String[] {}), spec)) {
							if (data.langLexerRef.containsKey(k)) {
								data.langLexerRef.get(k).add(lex);
							}
							else {
								data.langLexerRef.put(k, Collections.singletonList(lex));
							}
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
	public boolean registerDetectionType(DetectionType detectionType) {
		if (detectionType == null) {
			return false;
		}

		if (this.detectionTypeRegistry.containsKey(detectionType.getIdentifier())) {
			logger.error("DetectionType object with identifier '{}' already registered", detectionType.getIdentifier());
			return false;
		}

		this.detectionTypeRegistry.put(detectionType.getIdentifier(), detectionType);
		return true;
	}



	@Override
	public final boolean registerPostProcessor(Class<? extends IPostProcessor> postProcessor, Class<? extends AbstractModelTaskRawResult> handledResultType) {

		if (postProcessor == null || handledResultType == null) {
			logger.error("PostProcessor and/or AbstractModelTaskRawResult classes cannot be null");
			return false;
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

	//TODO: check the detection type used????????
	void analyseDetectors() {
		this.detectorRegistry.forEach((k, v) -> {
			Set<String> supportedLanguages = new HashSet<>();

			for (PreProcessingStrategy strat : v.strategies) {
				if (strat.isAdvanced()) {
					this.insersectSets(supportedLanguages, this.advPreProcessorRegistry.get(strat.getPreProcessorClasses().get(0).getName()).preProcessors.keySet());
				}
				else {
					Set<String> temp = this.languageRegistry.keySet().stream().filter(l -> this.getLexerForStrategy(strat, l) != null).collect(Collectors.toSet());
					this.insersectSets(supportedLanguages, temp);
				}
			}

			v.languages = supportedLanguages;
			supportedLanguages.forEach(l -> this.languageRegistry.get(l).detectors.add(k));
		});
	}

	void loadDetectionTypeWeights() {
		if (this.detectionTypeRegistry.size() == 0) {
			return;
		}

		Map<String, Float> map = null;
		int mapSize = 0;
		File weightFile = new File(SherlockEngine.configDir.getAbsolutePath() + File.separator + "Weightings.yaml");
		if (!weightFile.exists()) {
			map = new LinkedHashMap();
			mapSize = 0;
		}
		else {
			try {
				Yaml yaml = new Yaml();
				map = yaml.load(new FileInputStream(weightFile));
				mapSize = map.size();
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (map != null) {
			for (DetectionType type : this.detectionTypeRegistry.values()) {
				if (map.containsKey(type.getIdentifier())) {
					type.setWeighting(map.get(type.getIdentifier()));
				}
				else {
					map.put(type.getIdentifier(), type.getWeighting());
				}
			}

			if (map.size() != mapSize) {
				try {
					DumperOptions options = new DumperOptions();
					options.setPrettyFlow(true);
					Yaml yaml = new Yaml(options);
					FileWriter writer = new FileWriter(weightFile);
					yaml.dump(map, writer);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Verifies a a set of lexer channel conforms to a specification
	 *
	 * @param lexerChannels Set of channel names used in the lexer
	 * @param specification the specification to check
	 *
	 * @return does lexer meet specification?
	 */
	private boolean checkLexerAgainstSpecification(String[] lexerChannels, ILexerSpecification specification) {

		if (lexerChannels.length < specification.getChannelNames().length) {
			return false;
		}

		for (int i = 0; i < specification.getChannelNames().length; i++) {
			if (!lexerChannels[i].equals(specification.getChannelNames()[i]) && !specification.getChannelNames().equals("-")) {
				return false;
			}
		}

		return true;
	}

	private PostProcessorData getPostProcessorData(Class<? extends IPostProcessor> postProcessor) {
		return this.postProcRegistry.values().stream().filter(x -> x.proc.equals(postProcessor)).findFirst().orElse(null);
	}

	private void insersectSets(Set master, Set s2) {
		if (master.size() == 0) {
			master.addAll(s2);
		}
		else {
			master.retainAll(s2);
		}
	}

	private class DetectorData {

		String name;
		String desc;
		DetectorRank rank;
		Set<String> languages;
		List<PreProcessingStrategy> strategies;
		List<AdjustableParameterObj> adjustables;
		Class<? extends AbstractModelTaskRawResult> resultClass;

	}

	private class PostProcessorData {

		Class<? extends IPostProcessor> proc;
		List<AdjustableParameterObj> adjustables;
	}

	private class PreProcessorData {

		Map<String, List<Class<? extends Lexer>>> langLexerRef;

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
