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
import uk.ac.warwick.dcs.sherlock.api.SherlockHelper;
import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.executor.IExecutor;
import uk.ac.warwick.dcs.sherlock.engine.executor.PoolExecutor;
import uk.ac.warwick.dcs.sherlock.engine.storage.IStorageWrapper;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.BaseStorage;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class SherlockEngine {

	public static final String version = "@VERSION@";
	public static final Boolean enableExternalModules = true;

	public static Side side = Side.UNKNOWN;
	public static Configuration configuration = null;
	public static IStorageWrapper storage = null;
	public static IExecutor executor = null;

	static EventBus eventBus = null;
	static Registry registry = null;
	static String modulesPath = "";

	private static Logger logger = LoggerFactory.getLogger(SherlockEngine.class);
	private static File configDir;

	private File lockFile;
	private FileChannel lockChannel;
	private FileLock lock;
	private boolean valid;

	public SherlockEngine(Side side) {
		this.valid = false;
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
		SherlockEngine.side = side;

		SherlockEngine.setupConfigDir();

		this.lockFile = new File(SherlockEngine.configDir.getAbsolutePath() + File.separator + "Sherlock.lock");
		try {
			RandomAccessFile f = new RandomAccessFile(lockFile, "rw");

			if (f.length() == 0) {
				f.writeLong(System.currentTimeMillis());
			}

			System.out.println(f.length());
			this.lockChannel = f.getChannel();

			try {
				this.lock = this.lockChannel.tryLock();

				if (this.lock != null) {
					this.valid = true;
				}
			}
			catch (OverlappingFileLockException e) {
				this.valid = false;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		if (this.valid) {
			SherlockEngine.loadConfiguration();

			try {
				SherlockEngine.eventBus = new EventBus();
				Field field = uk.ac.warwick.dcs.sherlock.api.event.EventBus.class.getDeclaredField("bus");
				field.setAccessible(true);
				field.set(null, SherlockEngine.eventBus);

				SherlockEngine.registry = new Registry();
				field = SherlockRegistry.class.getDeclaredField("registry");
				field.setAccessible(true);
				field.set(null, SherlockEngine.registry);
			}
			catch (IllegalAccessException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Test the presence of a module at the passed classpath, should point to a class within the module
	 *
	 * @param classpath the module classpath as a string (to avoid requiring an import). Example for base Sherlock module: "uk.ac.warwick.dcs.sherlock.module.model.base.ModuleModelBase"
	 *
	 * @return Whether the module is present
	 */
	public static boolean isModulePresent(String classpath) {
		try {
			Class.forName(classpath);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	private static void loadConfiguration() {
		File configFile = new File(SherlockEngine.configDir.getAbsolutePath() + File.separator + "Sherlock.yaml");
		if (!configFile.exists()) {
			SherlockEngine.configuration = new Configuration();
			SherlockEngine.writeConfiguration(configFile);
		}
		else {
			try {
				Constructor constructor = new Constructor();
				constructor.addTypeDescription(new TypeDescription(Configuration.class, "!Sherlock"));
				Yaml yaml = new Yaml(constructor);
				SherlockEngine.configuration = yaml.loadAs(new FileInputStream(configFile), Configuration.class);
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static void setModulesPath(String classpath) {
		SherlockEngine.modulesPath = classpath;
	}

	private static void setupConfigDir() {
		SherlockEngine.configDir = new File(SystemUtils.IS_OS_WINDOWS ? System.getenv("APPDATA") + File.separator + "Sherlock" : System.getProperty("user.home") + File.separator + ".Sherlock");

		if (!SherlockEngine.configDir.exists()) {
			if (!SherlockEngine.configDir.mkdir()) {
				logger.error("Could not create dir: {}", SherlockEngine.configDir.getAbsolutePath());
				System.exit(1);
			}
		}
	}

	private static void writeConfiguration(File configFile) {
		try {
			Representer representer = new Representer();
			representer.addClassTag(Configuration.class, new Tag("!Sherlock"));
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

	public void initialise() {
		if (!this.valid) {
			logger.error("Cannot initialise SherlockEngine, is not valid. Likely an instance of Sherlock is already running");
			System.exit(1);
		}

		logger.info("Starting SherlockEngine on Side.{}", side.name());

		SherlockEngine.storage = new BaseStorage(); //expand to choose wrappers if we extend this
		SherlockEngine.executor = new PoolExecutor();

		try {
			Field field = SherlockHelper.class.getDeclaredField("sourceFileHelper");
			field.setAccessible(true);
			field.set(null, SherlockEngine.storage);

			field = SherlockHelper.class.getDeclaredField("codeBlockGroupClass");
			field.setAccessible(true);
			field.set(null, SherlockEngine.storage.getCodeBlockGroupClass());
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			logger.error("Could not set processed results class", e);
		}

		AnnotationLoader modules = new AnnotationLoader();
		modules.registerModules();

		SherlockEngine.eventBus.publishEvent(new EventPreInitialisation());
		SherlockEngine.eventBus.publishEvent(new EventInitialisation());
		SherlockEngine.registry.analyseDetectors();
		SherlockEngine.eventBus.publishEvent(new EventPostInitialisation());

		//Cleanup init events, we don't need them any more
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventPreInitialisation.class);
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventInitialisation.class);
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventPostInitialisation.class);
	}

	public boolean isValidInstance() {
		return this.valid;
	}

	private void shutdown() {
		logger.info("Stopping SherlockEngine");
		try {
			if (SherlockEngine.storage != null) {
				SherlockEngine.storage.close();
			}
			if (SherlockEngine.executor != null) {
				SherlockEngine.executor.shutdown();
			}

			if (this.lock != null) {
				this.lock.close();
			}
			if (this.lockChannel != null) {
				this.lockChannel.close();
			}
			if (this.lockFile != null) {
				this.lockFile.delete();
			}
		}
		catch (Exception ignored) {
		}
	}
}
