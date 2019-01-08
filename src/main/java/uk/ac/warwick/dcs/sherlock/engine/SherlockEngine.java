package uk.ac.warwick.dcs.sherlock.engine;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.core.Registry;
import uk.ac.warwick.dcs.sherlock.engine.core.SherlockConfiguration;
import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.BaseStorage;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class SherlockEngine {

	public static final String version = "@VERSION@";
	public static final Boolean enableExternalModules = false;

	public static Side side = Side.UNKNOWN;
	public static SherlockConfiguration configuration = null;
	public static IStorageWrapper storage = null;

	static EventBus eventBus = null;
	static RequestBus requestBus = null;
	static Registry registry = null;

	private static Logger logger = LoggerFactory.getLogger(SherlockEngine.class);
	private static File configDir;

	public SherlockEngine(Side side) {
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
		SherlockEngine.side = side;

		try {
			SherlockEngine.eventBus = new EventBus();
			Field field = uk.ac.warwick.dcs.sherlock.api.event.EventBus.class.getDeclaredField("bus");
			field.setAccessible(true);
			field.set(null, SherlockEngine.eventBus);

			SherlockEngine.requestBus = new RequestBus();
			field = uk.ac.warwick.dcs.sherlock.api.request.RequestBus.class.getDeclaredField("bus");
			field.setAccessible(true);
			field.set(null, SherlockEngine.requestBus);

			java.lang.reflect.Constructor construct = Registry.class.getDeclaredConstructor();
			construct.setAccessible(true);
			SherlockEngine.registry = (Registry) construct.newInstance();
			field = uk.ac.warwick.dcs.sherlock.api.common.SherlockRegistry.class.getDeclaredField("registry");
			field.setAccessible(true);
			field.set(null, SherlockEngine.registry);
			field = Registry.class.getDeclaredField("instance");
			field.setAccessible(true);
			field.set(null, SherlockEngine.registry);
		}
		catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void shutdown() {
		logger.info("Stopping SherlockEngine");
		try {
			SherlockEngine.storage.close();
		}
		catch (Exception ignored) {
		}
	}

	public void initialise() {
		logger.info("Starting SherlockEngine on Side.{}", side.name());
		SherlockEngine.loadConfiguration();
		SherlockEngine.storage = new BaseStorage(); //expand to choose wrappers if we extend this

		try {
			Field field = uk.ac.warwick.dcs.sherlock.api.common.SherlockHelper.class.getDeclaredField("modelProcessedResultsClass");
			field.setAccessible(true);
			field.set(null, SherlockEngine.storage.getModelProcessedResultsClass());

			field = uk.ac.warwick.dcs.sherlock.api.common.SherlockHelper.class.getDeclaredField("sourceFileHelper");
			field.setAccessible(true);
			field.set(null, SherlockEngine.storage);
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			logger.error("Could not set processed results class", e);
		}

		AnnotationLoader modules = new AnnotationLoader();
		modules.registerModules();
		modules.registerRequestProcessors();
		modules.registerResponseHandlers();

		SherlockEngine.eventBus.publishEvent(new EventPreInitialisation());
		SherlockEngine.eventBus.publishEvent(new EventInitialisation());
		SherlockEngine.eventBus.publishEvent(new EventPostInitialisation());

		//Cleanup init events, we don't need them any more
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventPreInitialisation.class);
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventInitialisation.class);
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventPostInitialisation.class);

		//SherlockEngine.eventBus.publishEvent(new EventPublishResults(runSherlockTest()));
		//uk.ac.warwick.dcs.sherlock.api.request.RequestBus.post(new RequestDatabase.RegistryRequests.GetDetectors().setPayload(""), this);
	}

	private static void loadConfiguration() {
		SherlockEngine.configDir = new File(SystemUtils.IS_OS_WINDOWS ? System.getenv("APPDATA") + File.separator + "Sherlock" : System.getProperty("user.home") + File.separator + ".Sherlock");

		logger.info(SherlockEngine.configDir.getAbsolutePath());

		if (!SherlockEngine.configDir.exists()) {
			if (!SherlockEngine.configDir.mkdir()) {
				logger.error("Could not create dir: {}", SherlockEngine.configDir.getAbsolutePath());
				return;
			}
		}

		File configFile = new File(SherlockEngine.configDir.getAbsolutePath() + File.separator + "Sherlock.yaml");
		if (!configFile.exists()) {
			SherlockEngine.configuration = new SherlockConfiguration();
			SherlockEngine.writeConfiguration();
		}
		else {
			try {
				Constructor constructor = new Constructor();
				constructor.addTypeDescription(new TypeDescription(SherlockConfiguration.class, "!Sherlock"));
				Yaml yaml = new Yaml(constructor);
				SherlockEngine.configuration = yaml.loadAs(new FileInputStream(configFile), SherlockConfiguration.class);
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private static void writeConfiguration() {
		File configFile = new File(SherlockEngine.configDir.getAbsolutePath() + File.separator + "Sherlock.yaml");
		try {
			Representer representer = new Representer();
			representer.addClassTag(SherlockConfiguration.class, new Tag("!Sherlock"));
			DumperOptions options = new DumperOptions();
			options.setPrettyFlow(true);
			Yaml yaml = new Yaml(representer, options);
			FileWriter writer = new FileWriter(configFile);
			yaml.dump(SherlockEngine.configuration, writer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*@ResponseHandler
	public void responseHandler(AbstractRequest request) {
		logger.info("got response: " + request.getResponse());
	}*/
}
