package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.component.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.ModelTaskProcessedResults;

import java.util.*;

public class SimpleObjectEqualityPostProcessor implements IPostProcessor<SimpleObjectEqualityRawResult> {

	@AdjustableParameter (name = "Test Param", defaultValue = 0, minimumBound = 0, maxumumBound = 10, step = 1)
	public int testParam;

	@Override
	public ModelTaskProcessedResults processResults(List<ISourceFile> files, List<SimpleObjectEqualityRawResult> rawResults) {
		ModelTaskProcessedResults results = new ModelTaskProcessedResults();
		Map<ISourceFile, Integer> totals = new HashMap<>();
		results.setFileTotals(totals);

		Map<Object, ICodeBlockGroup> map = new HashMap<>();
		for (SimpleObjectEqualityRawResult res : rawResults) {
			totals.putIfAbsent(res.getFile1(), res.getFile1NumObjects());
			totals.putIfAbsent(res.getFile2(), res.getFile2NumObjects());

			for (int i = 0; i < res.getSize(); i++) {
				Object o = res.getObject(i);
				ICodeBlockGroup group;

				if (!map.containsKey(o)) {
					group = results.addGroup();
					group.setComment("Variable: " + o.toString());
					try {
						group.setDetectionType("BASE_VARIABLE_NAME");
					}
					catch (UnknownDetectionTypeException e) {
						e.printStackTrace();
					}
					map.put(o, group);
				}
				else {
					group = map.get(o);
				}

				group.addCodeBlock(res.getFile1(), 1, res.getLocation(i).getPoint1()); //If file already present it will append to the existing files lines object
				group.addCodeBlock(res.getFile2(), 1, res.getLocation(i).getPoint2()); // ""
			}
		}

		return results;
	}
}
