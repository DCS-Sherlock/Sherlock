package uk.ac.warwick.dcs.sherlock.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotations.ResponseHandler;
import uk.ac.warwick.dcs.sherlock.api.common.IRequestReference;
import uk.ac.warwick.dcs.sherlock.api.common.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.common.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.common.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.util.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.model.TestResultsFactory;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SherlockEngine {

	public static final String version = "@VERSION@";
	public static final Boolean enableExternalModules = false;
	public static Side side = Side.UNKNOWN;

	static Logger logger = LoggerFactory.getLogger(SherlockEngine.class);
	static EventBus eventBus = null;
	static RequestBus requestBus = null;
	static Registry registry = null;

	public SherlockEngine(Side side) {
		SherlockEngine.side = side;

		try {
			SherlockEngine.eventBus = new EventBus();
			Field field = uk.ac.warwick.dcs.sherlock.api.common.EventBus.class.getDeclaredField("bus");
			field.setAccessible(true);
			field.set(null, SherlockEngine.eventBus);

			SherlockEngine.requestBus = new RequestBus();
			field = uk.ac.warwick.dcs.sherlock.api.common.RequestBus.class.getDeclaredField("bus");
			field.setAccessible(true);
			field.set(null, SherlockEngine.requestBus);

			SherlockEngine.registry = new Registry();
			field = uk.ac.warwick.dcs.sherlock.api.common.SherlockRegistry.class.getDeclaredField("registry");
			field.setAccessible(true);
			field.set(null, SherlockEngine.registry);
			field = uk.ac.warwick.dcs.sherlock.engine.Registry.class.getDeclaredField("instance");
			field.setAccessible(true);
			field.set(null, SherlockEngine.registry);
		}
		catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * old main method for reference
	 */
	private static String runSherlockTest() {
		String result = "";
		long startTime = System.currentTimeMillis();

		try {
			List<ISourceFile> fileList = Collections.synchronizedList(Arrays.asList(new TestResultsFactory.tmpFile("D:\\Work\\Uni\\GroupProject\\Sherlock\\out\\test.java"),
					new TestResultsFactory.tmpFile("D:\\Work\\Uni\\GroupProject\\Sherlock\\out\\test2.java")));
			result = TestResultsFactory.buildTestResults(fileList, TestDetector.class);
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}

		assert result != null;
		return result.concat("\n\nTotal Runtime Time = " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public void initialise() {
		logger.info("Starting SherlockEngine on Side.{}", side.name());

		AnnotationLoader modules = new AnnotationLoader();
		modules.registerModules();
		modules.registerRequestProcessors();
		modules.registerResponseHandlers();

		SherlockEngine.eventBus.publishEvent(new EventPreInitialisation());
		SherlockEngine.eventBus.publishEvent(new EventInitialisation());
		SherlockEngine.eventBus.publishEvent(new EventPostInitialisation());

		SherlockEngine.eventBus.removeInvocationsOfEvent(EventPreInitialisation.class);
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventInitialisation.class);
		SherlockEngine.eventBus.removeInvocationsOfEvent(EventPostInitialisation.class);

		//SherlockEngine.eventBus.registerEventSubscriber(this);
		//SherlockEngine.eventBus.publishEvent(new EventPublishResults(runSherlockTest()));
		//uk.ac.warwick.dcs.sherlock.api.common.RequestBus.post(RequestDatabase.RegistryRequests.GET_DETECTORS, this, "from the engine");
		//logger.info("t2" + uk.ac.warwick.dcs.sherlock.api.common.RequestBus.post(RequestDatabase.RegistryRequests.GET_DETECTORS, "from the engine 22"));
		SherlockEngine.registry.registerDetector(TestDetector.class);
	}

	@ResponseHandler
	public void responseHandler(IRequestReference request, Object response) {
		logger.info("got responce: " + response);
	}
}
