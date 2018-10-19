package uk.ac.warwick.dcs.sherlock.engine;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.model.TestResultsFactory;
import uk.ac.warwick.dcs.sherlock.lib.Reference;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;
//import uk.ac.warwick.dcs.sherlock.module.web.SherlockWeb;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SherlockEngine {

	public static void main(String[] args) {
		ModuleLoader modules = new ModuleLoader();
		/*for (Class<?> c : modules.getModules()) {
			System.out.println(c.getName());
		}*/

		modules.registerEventHandlers();

//		SherlockWeb.create();
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
