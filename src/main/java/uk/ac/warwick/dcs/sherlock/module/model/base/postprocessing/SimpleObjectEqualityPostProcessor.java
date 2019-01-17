package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.ModelTaskProcessedResults;
import uk.ac.warwick.dcs.sherlock.module.model.base.scoring.SimpleObjectEqualityScorer;

import java.util.*;

public class SimpleObjectEqualityPostProcessor implements IPostProcessor<SimpleObjectEqualityRawResult> {

	@AdjustableParameter (name = "Test Param", defaultValue = 0, minimumBound = 0, maxumumBound = 10, step = 1)
	public int testParam;

	@Override
	public ModelTaskProcessedResults processResults(List<ISourceFile> files, List<SimpleObjectEqualityRawResult> rawResults) {
		ModelTaskProcessedResults results = new ModelTaskProcessedResults(new SimpleObjectEqualityScorer());

		Map<Object, ICodeBlockGroup> map = new HashMap<>();
		for (SimpleObjectEqualityRawResult res : rawResults) {
			for (int i = 0; i < res.getSize(); i++) {
				Object o = res.getObject(i);
				ICodeBlockGroup group;

				if (!map.containsKey(o)) {
					group = results.addGroup();
					group.setComment("Variable: " + o.toString());
					map.put(o, group);
				}
				else {
					group = map.get(o);
				}

				group.addCodeBlock(res.getFile1(), 1, res.getLocation(i).getPoint1()); //If file already present it will append to the existing files lines object
				group.addCodeBlock(res.getFile2(), 1, res.getLocation(i).getPoint2()); // ""
			}
		}

		// do stuff in here

		// see docs, use:
		// x = results.addGroup();
		// x.addCodeBlock(..........); cont..

		return results;
	}
}
