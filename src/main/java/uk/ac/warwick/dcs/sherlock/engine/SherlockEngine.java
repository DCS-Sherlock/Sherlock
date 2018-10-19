package uk.ac.warwick.dcs.sherlock.engine;

import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.lib.Reference;
import uk.ac.warwick.dcs.sherlock.engine.model.TestResultsFactory;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static uk.ac.warwick.dcs.sherlock.api.event.EventBus.mapEventBus;

public class SherlockEngine {

	public static SherlockEngine instance = null;
	public static Reference.Side side = Reference.Side.UNKNOWN;

	static EventBus eventBus = null;

	public SherlockEngine(String[] args, Reference.Side side) {
		SherlockEngine.instance = this;
		SherlockEngine.side = side;
		SherlockEngine.eventBus = new EventBus();
		mapEventBus(SherlockEngine.eventBus);

		ModuleLoader modules = new ModuleLoader();
		modules.registerModuleEventHandlers();

		SherlockEngine.eventBus.publishEvent(new EventPreInitialisation(args));
		SherlockEngine.eventBus.publishEvent(new EventInitialisation());

		SherlockEngine.eventBus.removeInvokationsOfEvent(EventPreInitialisation.class);
		SherlockEngine.eventBus.removeInvokationsOfEvent(EventInitialisation.class);
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
