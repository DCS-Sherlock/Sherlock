package uk.ac.warwick.dcs.sherlock.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotations.ResponseHandler;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.request.IRequestReference;
import uk.ac.warwick.dcs.sherlock.api.request.RequestDatabase;
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

	public SherlockEngine(Side side) {
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
		}
		catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public void initalise() {
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
		uk.ac.warwick.dcs.sherlock.api.request.RequestBus.post(RequestDatabase.DataRequestReference.REQ1, this, "from the engine");
	}

	@ResponseHandler
	public void responceHandler(IRequestReference request, Object responce) {
		logger.info("got responce: " + responce);
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
}
