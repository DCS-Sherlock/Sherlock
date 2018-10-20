package uk.ac.warwick.dcs.sherlock.engine;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotations.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.lib.Reference;
import uk.ac.warwick.dcs.sherlock.engine.model.TestResultsFactory;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static uk.ac.warwick.dcs.sherlock.api.event.EventBus.mapEventBus;

public class SherlockEngine {

	public static Side side = Side.UNKNOWN;

	static final Log logger = LogFactory.getLog(SherlockEngine.class);
	static EventBus eventBus = null;

	public SherlockEngine(String[] args, Side side) {
		/** Disable logging until spring starts */
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.WARN);

		SherlockEngine.side = side;
		SherlockEngine.eventBus = new EventBus();
		mapEventBus(SherlockEngine.eventBus);

		ModuleLoader modules = new ModuleLoader();
		modules.registerModuleEventHandlers();

		SherlockEngine.eventBus.publishEvent(new EventPreInitialisation(args));
		SherlockEngine.eventBus.publishEvent(new EventInitialisation());

		SherlockEngine.eventBus.removeInvocationsOfEvent(EventPreInitialisation.class);
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventInitialisation.class);

		SherlockEngine.eventBus.registerEventSubscriber(this);
	}

	@EventHandler
	public void testSub(EventPreInitialisation event) {
		System.out.println("this should never print");
	}

	/**
	 * old main method for reference
	 */
	private String runSherlockTest() {
		if (Reference.isDevelEnv) {
			System.out.println("Sherlock vX.X.X [Development Version]\n");
		}
		else {
			System.out.println(String.format("Sherlock v%s\n", Reference.version));
		}

		String result = "";
		long startTime = System.currentTimeMillis();

		try {
			List<ISourceFile> fileList = Collections.synchronizedList(Arrays.asList(new TestResultsFactory.tmpFile("test.java"), new TestResultsFactory.tmpFile("test2.java")));
			result = TestResultsFactory.buildTestResults(fileList, TestDetector.class);
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}

		assert result != null;
		return result.concat("\n\nTotal Runtime Time = " + (System.currentTimeMillis() - startTime) + "ms");
	}
}
