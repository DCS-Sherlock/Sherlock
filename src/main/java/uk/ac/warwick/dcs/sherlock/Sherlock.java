package uk.ac.warwick.dcs.sherlock;

import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.lib.Reference;
import uk.ac.warwick.dcs.sherlock.model.base.detection.TestDetector;
import uk.ac.warwick.dcs.sherlock.model.core.TestResultsFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Sherlock {

	public static void main(String[] args) {
		if (Reference.isDevelEnv) {
			System.out.println("Sherlock vX.X.X [Development Version]\n");
		}
		else {
			System.out.println(String.format("Sherlock v%s\n", Reference.version));
		}

		/* temporary run in main method */

		long startTime = System.currentTimeMillis();

		try {
			List<ISourceFile> fileList = Collections.synchronizedList(Arrays.asList(new TestResultsFactory.tmpFile("test.java"), new TestResultsFactory.tmpFile("test2.java")));
			TestResultsFactory.buildTest(fileList, TestDetector.class);
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("Total Runtime Time = " + (endTime - startTime) + "ms");
	}

}
