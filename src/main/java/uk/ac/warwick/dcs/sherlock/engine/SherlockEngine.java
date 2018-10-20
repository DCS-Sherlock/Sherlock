package uk.ac.warwick.dcs.sherlock.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.model.TestResultsFactory;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static uk.ac.warwick.dcs.sherlock.api.event.EventBus.mapEventBus;

public class SherlockEngine {

	public static final String version = "@VERSION@";
	static final Logger logger = LoggerFactory.getLogger(SherlockEngine.class);
	public static Side side = Side.UNKNOWN;
	static EventBus eventBus = null;

	public SherlockEngine(Side side) {
		logger.info("Starting SherlockEngine on Side.{}", side.name());

		SherlockEngine.side = side;
		SherlockEngine.eventBus = new EventBus();
		mapEventBus(SherlockEngine.eventBus);

		ModuleLoader modules = new ModuleLoader();
		modules.registerModuleEventHandlers();

		SherlockEngine.eventBus.publishEvent(new EventPreInitialisation());
		SherlockEngine.eventBus.publishEvent(new EventInitialisation());
		SherlockEngine.eventBus.publishEvent(new EventPostInitialisation());

		SherlockEngine.eventBus.removeInvocationsOfEvent(EventPreInitialisation.class);
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventInitialisation.class);
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventPostInitialisation.class);

		//SherlockEngine.eventBus.registerEventSubscriber(this);
	}

	/**
	 * old main method for reference
	 */
	private String runSherlockTest() {
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
